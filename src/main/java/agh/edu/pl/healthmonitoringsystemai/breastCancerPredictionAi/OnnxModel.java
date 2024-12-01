package agh.edu.pl.healthmonitoringsystemai.breastCancerPredictionAi;

import agh.edu.pl.healthmonitoringsystemai.client.HuggingFaceClient;
import agh.edu.pl.healthmonitoringsystemai.exception.AwsApiException;
import ai.onnxruntime.*;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

@Slf4j
@Component
@DependsOn("huggingFaceClient")
public class OnnxModel {
    // TODO: Change automatically choice -> from s3 or not (aws client to create)

    private static final String MODEL_PATH = "model.onnx";
    private static final String BUCKET_NAME = "model-bucket-2025"; //TODO: Remove hardcoded
    private static final String S3_MODEL_KEY = "model.onnx";

    private final OrtEnvironment env;
    private final OrtSession session;
    private final AmazonS3 s3Client;

    @Autowired
    public OnnxModel(HuggingFaceClient huggingFaceClient) throws OrtException {
        this.env = OrtEnvironment.getEnvironment();
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                .withRegion("us-east-1")
                .build();

        if (!isModelPresent()) {
            log.info("Model not found locally. Downloading from S3...");
            downloadModelFromS3();
        }

        this.session = env.createSession(MODEL_PATH, new OrtSession.SessionOptions());
    }

    private boolean isModelPresent() {
        Path modelFile = Paths.get(MODEL_PATH);
        return Files.exists(modelFile);
    }

    private void downloadModelFromS3() {
        try {
            File modelFile = new File(MODEL_PATH);

            S3Object s3Object = s3Client.getObject(new GetObjectRequest(BUCKET_NAME, S3_MODEL_KEY));

            try (S3ObjectInputStream s3InputStream = s3Object.getObjectContent()) {
                Files.copy(s3InputStream, modelFile.toPath());
            }

            log.info("Model downloaded successfully from S3.");
        } catch (Exception e) {
            log.error("Failed to save model", e);
            throw new AwsApiException("Could not download or save model from S3: " + e.getMessage());
        }
    }

    public float[][] run(float[][][][] inputData) throws OrtException {
        OnnxTensor inputTensor = OnnxTensor.createTensor(env, inputData);
        OrtSession.Result result = session.run(Collections.singletonMap("input", inputTensor));
        return (float[][]) result.get(0).getValue();
    }

    @PreDestroy
    public void close() throws OrtException {
        log.info("Closing ONNX session and environment.");
        session.close();
        env.close();
    }
}
