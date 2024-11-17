package agh.edu.pl.healthmonitoringsystemai.client;

import agh.edu.pl.healthmonitoringsystemai.exception.HuggingFaceApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@Component
public class HuggingFaceClient {

    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 5000;
    private static final String MODEL_PATH = "model.onnx";

    private final String modelApiUrl;
    private final String apiToken;

    public HuggingFaceClient(@Value("${huggingface.ai.api.url}") String modelApiUrl,
                             @Value("${huggingface.ai.api.token}") String apiToken) {
        this.modelApiUrl = modelApiUrl;
        this.apiToken = apiToken;
    }

    public void downloadModel() throws HuggingFaceApiException {
        log.info("Fetching Breast Cancer Prediction AI Model...");

        HttpURLConnection connection = null;
        try {
            connection = createConnection();
            validateResponse(connection);

            downloadAndSaveModel(connection);
            log.info("Model downloaded successfully to {}", MODEL_PATH);

        } catch (Exception e) {
            log.error("Error downloading the model: {}", e.getMessage(), e);
            throw new HuggingFaceApiException("Error downloading model from Hugging Face API. " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private HttpURLConnection createConnection() throws IOException {
        URL url = new URL(modelApiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(CONNECTION_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);
        connection.setRequestProperty("Authorization", "Bearer " + apiToken);
        return connection;
    }

    private void validateResponse(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            log.error("Failed to fetch the model. HTTP Response Code: {}", responseCode);
            throw new HuggingFaceApiException("Failed to fetch the model, HTTP Response Code: " + responseCode);
        }
    }

    private void downloadAndSaveModel(HttpURLConnection connection) throws IOException {
        try (InputStream inputStream = connection.getInputStream();
             FileOutputStream fileOutputStream = new FileOutputStream(MODEL_PATH)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
        }
    }
}
