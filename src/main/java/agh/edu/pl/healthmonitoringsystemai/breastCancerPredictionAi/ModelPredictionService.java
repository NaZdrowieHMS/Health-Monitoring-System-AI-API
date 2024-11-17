package agh.edu.pl.healthmonitoringsystemai.breastCancerPredictionAi;

import agh.edu.pl.healthmonitoringsystem.response.ResultDataContent;
import agh.edu.pl.healthmonitoringsystemai.exception.PredictionException;
import ai.onnxruntime.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class ModelPredictionService {

    private final ImagePreprocessor imagePreprocessor;
    private final OnnxModel aiModel;
    private static final String[] CLASS_NAMES = {"benign", "malignant", "normal"};

    @Autowired
    public ModelPredictionService(ImagePreprocessor imagePreprocessor, OnnxModel aiModel) {
        this.imagePreprocessor = imagePreprocessor;
        this.aiModel = aiModel;
    }

    public PredictionResult predict(ResultDataContent resultDataContent) {
        try {
            float[][][][] imageData = imagePreprocessor.preprocessImage(resultDataContent.data());
            float[][] predictions = runInference(imageData);
            PredictionResult result = postProcessPredictions(predictions);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            throw new PredictionException(e.getMessage());
        }
    }

    private float[][] runInference(float[][][][] imageData) throws OrtException {
        return aiModel.run(imageData);
    }

    private PredictionResult postProcessPredictions(float[][] predictions) {
        int predictedClassIndex = getPredictedClassIndex(predictions[0]);
        double confidence = predictions[0][predictedClassIndex];
        return new PredictionResult(CLASS_NAMES[predictedClassIndex], confidence * 100);
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