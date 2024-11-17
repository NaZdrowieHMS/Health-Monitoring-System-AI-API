package agh.edu.pl.healthmonitoringsystemai.breastCancerPredictionAi;

import agh.edu.pl.healthmonitoringsystemai.client.HuggingFaceClient;
import ai.onnxruntime.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

@Component
public class OnnxModel {

    private static final String MODEL_PATH = "model.onnx";
    private final OrtEnvironment env;
    private final OrtSession session;

    @Autowired
    public OnnxModel(HuggingFaceClient huggingFaceClient) throws OrtException, IOException {
        this.env = OrtEnvironment.getEnvironment();

        if (!isModelPresent()) {
            huggingFaceClient.downloadModel();
        }

        this.session = env.createSession(MODEL_PATH, new OrtSession.SessionOptions());
    }

    private boolean isModelPresent() {
        Path modelFile = Paths.get(MODEL_PATH);
        return Files.exists(modelFile);
    }

    public float[][] run(float[][][][] inputData) throws OrtException {
        OnnxTensor inputTensor = OnnxTensor.createTensor(env, inputData);
        OrtSession.Result result = session.run(Collections.singletonMap("input", inputTensor));
        return (float[][]) result.get(0).getValue();
    }
}

