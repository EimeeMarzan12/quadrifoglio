package com.example.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import com.google.common.util.concurrent.ListenableFuture;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CameraFragment extends Fragment {
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ObjectDetectionModel objectDetectionModel;
    private TextView detectionResultsTextView;
    private DrawView drawView;
    private PreviewView previewView;
    private boolean isDetecting = true;
    private View view;
    private static final int MODEL_INPUT_SIZE = 448; // Change to your model's input size

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_camera, container, false);

        detectionResultsTextView = view.findViewById(R.id.resultsTextView);
        Button stopDetectionButton = view.findViewById(R.id.stopButton);
        previewView = view.findViewById(R.id.cameraPreview);
        drawView = view.findViewById(R.id.drawView);

        stopDetectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopDetection();
            }
        });


        Log.d("!!!", "try");

        // Initialize ObjectDetectionModel (adjust model path as needed)
        try {
            objectDetectionModel = new ObjectDetectionModel(
                    requireContext().getAssets(),
                    "D:\\Program Codes\\thesisproject\\Test\\Test\\app\\src\\main\\assets\\model.tflite"
            );
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Initialize camera
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, requireContext().getMainExecutor());

        Log.d("!!!", "Try 2");
        return view;
    }

    private void bindPreview(ProcessCameraProvider cameraProvider) {
        int aspectRatio = aspectRatio(previewView.getWidth(), previewView.getHeight());
        Preview preview = new Preview.Builder().setTargetAspectRatio(aspectRatio).build();

        ImageCapture imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetRotation(getActivity().getWindowManager().getDefaultDisplay().getRotation()).build();

        CameraSelector cameraSelector = new CameraSelector.Builder().build();

        cameraProvider.unbindAll();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(MODEL_INPUT_SIZE, MODEL_INPUT_SIZE))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(requireContext().getMainExecutor(), new ImageAnalysis.Analyzer() {
                    @Override
                    public void analyze(@NonNull ImageProxy imageProxy) {
                        if (isDetecting) {
                            detectObjects(imageProxy);
                        }
                    }
                }
        );

        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageAnalysis);
    }

    private void detectObjects(ImageProxy imageProxy) {
        // Convert ImageProxy to TensorImage
        TensorImage inputImage = convertImageToInputFormat(imageProxy);

        // Run inference
        float[][][][] input = preprocessInput(inputImage);
//        List<ObjectDetectionModel.DetectionResult> results = objectDetectionModel.runInference(input);
        float[] results = objectDetectionModel.runInference(input);
        Log.d("!!!", Arrays.toString(input));
        Log.d("!!!", Arrays.toString(results));

        // Process results and update UI
//        updateUIWithResults(results);
        imageProxy.close();
    }

    private TensorImage convertImageToInputFormat(ImageProxy imageProxy) {
        // Initialize a TensorImage object (part of TensorFlow Lite support library)
        TensorImage inputImage = new TensorImage();

        // Get the image from ImageProxy
        ByteBuffer buffer = imageProxy.getPlanes()[0].getBuffer();
        Bitmap bitmap = Bitmap.createBitmap(MODEL_INPUT_SIZE, MODEL_INPUT_SIZE, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        inputImage.load(bitmap);

        // Preprocess the image to the correct size
        inputImage = new ImageProcessor.Builder()
                .add(new ResizeOp(MODEL_INPUT_SIZE, MODEL_INPUT_SIZE, ResizeOp.ResizeMethod.BILINEAR))
                .build()
                .process(inputImage);

        return inputImage;
    }

    private float[][][][] preprocessInput(TensorImage inputImage) {
        ByteBuffer byteBuffer = inputImage.getBuffer();
        float[][][][] input = new float[1][MODEL_INPUT_SIZE][MODEL_INPUT_SIZE][3];
        for (int y = 0; y < MODEL_INPUT_SIZE; y++) {
            for (int x = 0; x < MODEL_INPUT_SIZE; x++) {
                input[0][y][x][0] = (byteBuffer.get() & 0xFF) / 255.0f;
                input[0][y][x][1] = (byteBuffer.get() & 0xFF) / 255.0f;
                input[0][y][x][2] = (byteBuffer.get() & 0xFF) / 255.0f;
            }
        }

        return input;
    }

    private void updateUIWithResults(List<ObjectDetectionModel.DetectionResult> results) {
        // Clear previous drawings on DrawView
        drawView.clear();

        // Draw bounding boxes for each detected object
        for (ObjectDetectionModel.DetectionResult result : results) {
            // Calculate bounding box coordinates (left, top, right, bottom)
            float left = result.left * drawView.getWidth();
            float top = result.top * drawView.getHeight();
            float right = result.right * drawView.getWidth();
            float bottom = result.bottom * drawView.getHeight();

            // Draw bounding box on DrawView
            drawView.drawBoundingBox(left, top, right, bottom, result.label);
        }
    }

    private void stopDetection() {
        isDetecting = false;
        // Show final results in TextView
        detectionResultsTextView.setText("Detection stopped. Results:\n\n" + getCurrentResultsAsString());
    }

    private String getCurrentResultsAsString() {
        // Create a string representation of current detection results
        // Example: Concatenate class names and scores
        StringBuilder resultString = new StringBuilder();
        List<ObjectDetectionModel.DetectionResult> results = objectDetectionModel.getResults();
        for (ObjectDetectionModel.DetectionResult result : results) {
            resultString.append(result.label).append(": ").append(result.score).append("\n");
        }
        return resultString.toString();
    }

    private int aspectRatio(int width, int height) {
        double previewRatio = (double) Math.max(width, height) / Math.min(width, height);
        if (Math.abs(previewRatio - 4.0 / 3.0) <= Math.abs(previewRatio - 16.0 / 9.0)) {
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;
    }
}
