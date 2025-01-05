package agh.edu.pl.healthmonitoringsystemai.breastCancerPredictionAi;

import agh.edu.pl.healthmonitoringsystem.client.PredictionApi;
import agh.edu.pl.healthmonitoringsystem.enums.PredictionRequestStatus;
import agh.edu.pl.healthmonitoringsystem.model.FormAiAnalysis;
import agh.edu.pl.healthmonitoringsystem.model.ResultAiData;
import agh.edu.pl.healthmonitoringsystem.request.PredictionSummaryRequest;
import agh.edu.pl.healthmonitoringsystem.request.PredictionSummaryUpdateRequest;
import agh.edu.pl.healthmonitoringsystem.request.PredictionUploadRequest;
import agh.edu.pl.healthmonitoringsystem.response.Prediction;
import agh.edu.pl.healthmonitoringsystem.response.PredictionSummary;
import agh.edu.pl.healthmonitoringsystem.response.ResultDataContent;
import agh.edu.pl.healthmonitoringsystemai.client.RetrofitClient;
import agh.edu.pl.healthmonitoringsystemai.exception.ApiException;
import agh.edu.pl.healthmonitoringsystemai.exception.PredictionException;
import agh.edu.pl.healthmonitoringsystemai.mistralAi.MistralAIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PredictionRequestService {
    private final PredictionApi predictionApi;
    private final ModelPredictionService modelPredictionService;

    private final MistralAIService mistralAiService;

    @Autowired
    public PredictionRequestService(RetrofitClient retrofitClient, ModelPredictionService modelPredictionService, MistralAIService mistralAiService) {
        this.predictionApi = retrofitClient.getRetrofitClient().create(PredictionApi.class);
        this.modelPredictionService = modelPredictionService;
        this.mistralAiService = mistralAiService;
    }

    public RequestResponse createPredictionRequest(PredictionSummaryRequest request, String authentication) {
        log.info("Creating prediction request");
        try {
            Response<PredictionSummary> response = predictionApi.createPredictionRequest(request, authentication).execute();
            System.out.println(response.body());
            if (!response.isSuccessful() || response.body() == null) {
                throw new ApiException("Error during creation of prediction request: " + response.errorBody());
            }

            PredictionSummary predictionSummary = response.body();
            processPredictionAsync(predictionSummary, authentication);
            return new RequestResponse(predictionSummary.id());

        } catch (Exception e) {
            log.error("Exception during creation of prediction request: {}", e.getMessage());
            throw new ApiException("Exception error during creation of prediction request: " + e.getMessage());
        }
    }

    public PredictionSummary getPredictionStatus(Long requestId, String authentication) {
        log.info("Fetching prediction request");
        try {
            Response<PredictionSummary> response = predictionApi.getPredictionSummaryRequestById(requestId, authentication).execute();
            if (!response.isSuccessful() || response.body() == null) {
                throw new ApiException("Error during fetching prediction request: " + response.errorBody());
            }
            return response.body();

        } catch (Exception e) {
            log.error("Unexpected error during fetching prediction request", e);
            throw new ApiException("Unexpected error during fetching of prediction request: " + e.getMessage());
        }
    }

    @Async
    protected void processPredictionAsync(PredictionSummary predictionSummary, String authentication) {
            List<Double> confidences = new ArrayList<>();
            List<String> predictions = new ArrayList<>();

        List<Long> resultIds = predictionSummary.resultAiAnalysis().results().stream()
                .map(ResultAiData::resultId)
                .toList();

            for (Long resultId : resultIds) {
                PredictionResult prediction = processResult(resultId, predictionSummary, authentication);

                confidences.add(prediction.confidence());
                predictions.add(prediction.prediction());
            }

            FormAiAnalysis formPrediction = mistralAiService.getAiAnalysisBasedOnForm(predictionSummary.patientId(), predictionSummary.doctorId());

            if (confidences.isEmpty()) {
                updatePredictionRequestStatus(predictionSummary.id(), PredictionRequestStatus.FAILED, null, null, null, authentication);
                throw new PredictionException("No predictions were processed for request ID: " + predictionSummary.id());
            }

            completePredictionRequest(predictionSummary.id(), confidences, predictions, formPrediction, authentication);
    }

    private void completePredictionRequest(Long predictionRequestId, List<Double> confidences, List<String> predictions, FormAiAnalysis formAiAnalysis, String authentication) {
        double averageConfidence = countAverageConfidence(confidences);
        String finalPrediction = predictions.get(confidences.indexOf(Collections.max(confidences)));

        updatePredictionRequestStatus(predictionRequestId, PredictionRequestStatus.COMPLETED, averageConfidence, finalPrediction, formAiAnalysis, authentication);
        log.info(String.format("Prediction completed for prediction request with id %s", predictionRequestId));
    }

    private PredictionResult processResult(Long resultId, PredictionSummary predictionSummary, String authentication) {
        try {
            Response<Prediction> response = predictionApi.getPredictionForResult(resultId, authentication).execute();

            if (response.isSuccessful() && response.body() != null) {
                Prediction existingPrediction = response.body();
                return new PredictionResult(existingPrediction.prediction(), existingPrediction.confidence());
            } else {
                handleErrorResponse(response, "Fetching prediction for resultId: " + resultId);
            }
        } catch (EOFException e)   {
            log.info("Prediction for result with id {} does not exist. Processing prediction.", resultId);
        } catch (Exception e) {
            log.error("Error processing resultId: {}", resultId, e);
        }
        return processNewPredictionForResult(resultId, predictionSummary, authentication);
    }

    private void handleErrorResponse(Response<?> response, String context) throws ApiException {
        String errorBody = response.errorBody() != null ? String.valueOf(response.errorBody()) : "Unknown error";
        log.error("API error during {}: {}, status: {}", context, errorBody, response.code());
        throw new ApiException(String.format("API error during %s: %s", context, errorBody), response.code());
    }

    private PredictionResult processNewPredictionForResult(Long resultId, PredictionSummary predictionSummary, String authentication) {
        try {
            ResultDataContent resultData = predictionApi.getPredictionDataFromResult(resultId, authentication).execute().body();
            if (resultData == null) {
                throw new PredictionException("No data available for resultId: " + resultId);
            }

            PredictionResult newPredictionResult = modelPredictionService.predict(resultData);

            PredictionUploadRequest uploadRequest = new PredictionUploadRequest(
                    resultId,
                    predictionSummary.doctorId(),
                    newPredictionResult.confidence(),
                    newPredictionResult.prediction()
            );
            Response<Prediction> response = predictionApi.uploadPrediction(uploadRequest, authentication).execute();

            if (response.isSuccessful() && response.body() != null) {
                return newPredictionResult;
            } else {
                handleErrorResponse(response, "Uploading prediction for resultId: " + resultId);
            }
        } catch (Exception ex) {
            log.error("Error processing new prediction for resultId: {}", resultId, ex);
        }
        return new PredictionResult("unknown", 0.0);
    }


    private void updatePredictionRequestStatus(Long requestId, PredictionRequestStatus predictionRequestStatus,
                                               Double confidence, String prediction, FormAiAnalysis formAiAnalysis, String authentication){
        try {
            PredictionSummaryUpdateRequest updateRequest = new PredictionSummaryUpdateRequest(
                    requestId,
                    predictionRequestStatus,
                    confidence,
                    prediction,
                    formAiAnalysis
            );
            Response<Void> response = predictionApi.updatePredictionRequest(updateRequest, authentication).execute();
            if (!response.isSuccessful()) {
                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                throw new ApiException(String.format("Error updating prediction request: %s", errorBody), response.code());
            }
        } catch (Exception e) {
            String message = String.format("Failed to update prediction summary to %s status: %s", predictionRequestStatus.toString(),
                    e.getMessage());
            log.error(message, e);
            throw new PredictionException(message);
        }
    }

    private double countAverageConfidence(List<Double> confidences){
        return confidences.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }
}
