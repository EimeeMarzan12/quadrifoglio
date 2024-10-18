package com.example.test;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;

import org.tensorflow.lite.Interpreter;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ObjectDetectionModel {
    private static final int MODEL_INPUT_SIZE = 256;
    private static final int NUM_CLASSES = 5; // Adjust according to your model's output
    private Interpreter tflite;
    private List<String> labels;
    private List<DetectionResult> currentResults;

    public ObjectDetectionModel(AssetManager assetManager, String modelPath) throws IOException {
        MappedByteBuffer tfliteModel = loadModelFile(assetManager, modelPath);
        tflite = new Interpreter(tfliteModel);
        labels = loadLabels(assetManager, "labels.txt"); // Ensure you have a labels file in assets
        currentResults = new ArrayList<>();
    }

    private MappedByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

//    private MappedByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws IOException {
//        InputStream inputStream = assetManager.open(modelPath);
//        byte[] modelBuffer = new byte[inputStream.available()];
//        inputStream.read(modelBuffer);
//        inputStream.close();
//        return (MappedByteBuffer) ByteBuffer.wrap(modelBuffer);
//    }

    private List<String> loadLabels(AssetManager assetManager, String fileName) throws IOException {
        List<String> labels = new ArrayList<>();
        InputStream inputStream = assetManager.open(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            labels.add(line);
        }
        reader.close();
        return labels;
    }

    public float[] runInference(float[][][][] input) {
        float[][] output = new float[1][NUM_CLASSES];
        tflite.run(input, output);
        return output[0];
    }

    private float processOutput(float[] output) {
//        List<DetectionResult> results = new ArrayList<>();
        float max = 0;
        for (int i = 0; i < output.length; i++) {
            if(i < output.length - 1 && output[i] >= output[i+1]){
                max = i;
            }

//            int classId = (int) detection[0];
//            float score = detection[1];
//            float left = detection[2];
//            float top = detection[3];
//            float right = detection[4];
//            float bottom = detection[5];
//
//            if (score > 0.5) { // Threshold for considering a detection
//                String label = labels.get(classId);
//                results.add(new DetectionResult(classId, label, score, left, top, right, bottom));
//            }
        }
        return max;
    }

    public List<DetectionResult> getResults() {
        return currentResults;
    }

    public static class DetectionResult {
        public final int classId;
        public final String label;
        public final float score;
        public final float left;
        public final float top;
        public final float right;
        public final float bottom;

        public DetectionResult(int classId, String label, float score, float left, float top, float right, float bottom) {
            this.classId = classId;
            this.label = label;
            this.score = score;
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }
    }
}
