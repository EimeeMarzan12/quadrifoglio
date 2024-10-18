package com.example.test;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.ImageFormat;
import android.graphics.Bitmap;
import androidx.camera.core.ImageProxy;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


import android.graphics.YuvImage;
import android.graphics.Rect;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;

import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.common.util.concurrent.ListenableFuture;

import org.tensorflow.lite.Interpreter;


import java.util.concurrent.ExecutionException;

public class CameraFragment extends Fragment {
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private Interpreter tflite;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        previewView = view.findViewById(R.id.cameraPreview);

        requestCameraPermissions();

        // Load the model when the fragment view is created
        try {
            MappedByteBuffer modelBuffer = loadModelFile();
            tflite = new Interpreter(modelBuffer);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("CameraFragment", "Error loading model: " + e.getMessage());
        }
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (tflite != null) {
            tflite.close();
        }
    }

    private void requestCameraPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            startCamera();  // Start the camera if permission already granted
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera(); // Start the camera if permission granted
            } else {
                Log.e("CameraFragment", "Camera permission denied");
                // Optional: Show a dialog to the user explaining why the permission is needed
            }
        }
    }

    private void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Log.e("CameraFragment", "Error initializing camera: " + e.getMessage());
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void bindPreview(ProcessCameraProvider cameraProvider) {
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        Preview preview = new Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(this, cameraSelector, preview);

        // Set up Image Analysis for model inference
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(640, 640))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_BLOCK_PRODUCER)
                .build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(requireContext()), this::analyzeImage);
        cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis);
    }

    private void analyzeImage(ImageProxy image) {
        // Convert ImageProxy to Bitmap
        Bitmap bitmap = imageToBitmap(image);

        // Check if bitmap is null before proceeding
        if (bitmap == null) {
            Log.e("CameraFragment", "Bitmap conversion failed");
            image.close(); // Close the image when done
            return;
        }

        // Prepare input and output buffers
        float[][][] input = preprocessImage(bitmap);
        float[][] output = new float[1][1]; // Adjust according to your model's output shape

        // Run the model
        tflite.run(input, output);

        // Do something with the output
        Log.d("CameraFragment", "Model output: " + output[0][0]);

        image.close(); // Close the image when done
    }

    private Bitmap imageToBitmap(ImageProxy image) {
        Image mediaImage = image.getImage();
        if (mediaImage == null) {
            Log.e("CameraFragment", "MediaImage is null");
            return null;
        }

        // Get the YUV bytes from the ImageProxy
        Image.Plane[] planes = mediaImage.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer(); // Y
        ByteBuffer uBuffer = planes[1].getBuffer(); // U
        ByteBuffer vBuffer = planes[2].getBuffer(); // V

        // Get the byte arrays
        byte[] yBytes = new byte[yBuffer.remaining()];
        yBuffer.get(yBytes);
        byte[] uBytes = new byte[uBuffer.remaining()];
        uBuffer.get(uBytes);
        byte[] vBytes = new byte[vBuffer.remaining()];
        vBuffer.get(vBytes);

        // Calculate the image dimensions
        int width = image.getWidth();
        int height = image.getHeight();

        // Create a YUV Image
        YuvImage yuvImage = new YuvImage(yBytes, ImageFormat.NV21, width, height, null);

        // Create a ByteArrayOutputStream to hold the bitmap
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // Compress the YUV image into JPEG format
        yuvImage.compressToJpeg(new Rect(0, 0, width, height), 100, outputStream);

        // Get the compressed JPEG data as a byte array
        byte[] jpegData = outputStream.toByteArray();

        // Create a Bitmap from the JPEG data
        return BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length);
    }



    private float[][][] preprocessImage(Bitmap bitmap) {
        // Resize the Bitmap to the required input size (640x640)
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 640, 640, true);

        // Create an array to hold the normalized pixel values
        float[][][] input = new float[3][640][640];

        // Loop through each pixel to fill the input array
        for (int y = 0; y < 640; y++) {
            for (int x = 0; x < 640; x++) {
                int pixel = resizedBitmap.getPixel(x, y);
                // Extract the RGB values
                float r = (float) ((pixel >> 16) & 0xff) / 255.0f; // Normalize to [0, 1]
                float g = (float) ((pixel >> 8) & 0xff) / 255.0f;  // Normalize to [0, 1]
                float b = (float) (pixel & 0xff) / 255.0f;         // Normalize to [0, 1]

                // Assign to the input array
                input[0][y][x] = r; // Red channel
                input[1][y][x] = g; // Green channel
                input[2][y][x] = b; // Blue channel
            }
        }

        return input; // Return the preprocessed input
    }


    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = getContext().getAssets().openFd("model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();

        // Map the model file into memory
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
}
