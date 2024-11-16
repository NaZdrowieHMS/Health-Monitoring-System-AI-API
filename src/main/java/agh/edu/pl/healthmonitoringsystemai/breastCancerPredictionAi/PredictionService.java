package agh.edu.pl.healthmonitoringsystemai.breastCancerPredictionAi;

import agh.edu.pl.healthmonitoringsystemai.exception.PredictionException;
import ai.onnxruntime.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PredictionService {

    private final ImagePreprocessor imagePreprocessor;
    private final OnnxModel aiModel;
    private static final String[] CLASS_NAMES = {"benign", "malignant", "normal"};

    @Autowired
    public PredictionService(ImagePreprocessor imagePreprocessor, OnnxModel aiModel) {
        this.imagePreprocessor = imagePreprocessor;
        this.aiModel = aiModel;
    }

    public void predict(PredictionRequest request) {
        // TODO returned value should be Prediction (not void)
        try {
            float[][][][] imageData = preprocessImage(request);
            float[][] predictions = runInference(imageData);
            String result = postProcessPredictions(predictions);
            System.out.println(result);
        } catch (OrtException e) {
            throw new PredictionException(e.getMessage());
        }
    }

    private float[][][][] preprocessImage(PredictionRequest request) {
        return imagePreprocessor.preprocessImage(request.getImageBase64());
    }

    private float[][] runInference(float[][][][] imageData) throws OrtException {
        return aiModel.run(imageData);
    }

    private String postProcessPredictions(float[][] predictions) {
        int predictedClassIndex = getPredictedClassIndex(predictions[0]);
        float confidence = predictions[0][predictedClassIndex];
        return String.format("Predicted class: %s\nConfidence: %.2f%%",
                CLASS_NAMES[predictedClassIndex], confidence * 100);
    }

    private int getPredictedClassIndex(float[] predictions) {
        int index = 0;
        float maxValue = predictions[0];
        for (int i = 1; i < predictions.length; i++) {
            if (predictions[i] > maxValue) {
                maxValue = predictions[i];
                index = i;
            }
        }
        return index;
    }
}