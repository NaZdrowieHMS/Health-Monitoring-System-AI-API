package agh.edu.pl.healthmonitoringsystemai.breastCancerPredictionAi;

import agh.edu.pl.healthmonitoringsystem.client.PredictionApi;
import agh.edu.pl.healthmonitoringsystem.enums.PredictionRequestStatus;
import agh.edu.pl.healthmonitoringsystem.request.PredictionSummaryRequest;
import agh.edu.pl.healthmonitoringsystem.request.PredictionSummaryUpdateRequest;
import agh.edu.pl.healthmonitoringsystem.request.PredictionUploadRequest;
import agh.edu.pl.healthmonitoringsystem.response.Prediction;
import agh.edu.pl.healthmonitoringsystem.response.PredictionSummary;
import agh.edu.pl.healthmonitoringsystem.response.ResultDataContent;
import agh.edu.pl.healthmonitoringsystemai.client.RetrofitClient;
import agh.edu.pl.healthmonitoringsystemai.exception.ApiException;
import agh.edu.pl.healthmonitoringsystemai.exception.PredictionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PredictionRequestService {
    private final PredictionApi predictionApi;
    private final ModelPredictionService modelPredictionService;

    @Autowired
    public PredictionRequestService(RetrofitClient retrofitClient, ModelPredictionService modelPredictionService) {
        this.predictionApi = retrofitClient.getRetrofitClient().create(PredictionApi.class);
        this.modelPredictionService = modelPredictionService;
    }


    public RequestResponse createPredictionRequest(PredictionSummaryRequest request) {
        log.info("Creating prediction request");
        try {
            Response<PredictionSummary> response = predictionApi.createPredictionRequest(request).execute();
            if (!response.isSuccessful() || response.body() == null) {
                throw new ApiException("Error during creation of prediction request: " + response.errorBody());
            }
            PredictionSummary predictionSummary = response.body();
            processPredictionAsync(predictionSummary);
            return new RequestResponse(predictionSummary.id());

        } catch (Exception e) {
            log.error("Unexpected error during creation of prediction request", e);
            throw new ApiException("Unexpected error during creation of prediction request: " + e.getMessage());
        }
    }


    public PredictionSummary getPredictionStatus(Long requestId) {
        log.info("Fetching prediction request");
        try {
            Response<PredictionSummary> response = predictionApi.getPredictionSummaryRequestById(requestId).execute();
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
    protected void processPredictionAsync(PredictionSummary predictionSummary) {
            List<Double> confidences = new ArrayList<>();
            List<String> predictions = new ArrayList<>();
            Long requestId = predictionSummary.id();

            for (Long resultId : predictionSummary.resultIds()) {
                try {
                    Prediction existingPrediction = predictionApi.getPredictionForResult(resultId).execute().body();
                    if (existingPrediction != null) {
                        confidences.add(existingPrediction.confidence());
                        predictions.add(existingPrediction.prediction());
                        }
                } catch (Exception e) {
                    log.error("Error processing resultId: {}", resultId, e);
//                }
//
//                    if (existingPrediction != null) {
//                        confidences.add(existingPrediction.confidence());
//                        predictions.add(existingPrediction.prediction());
//                    } else {

                    try{
                        ResultDataContent resultData = predictionApi.getPredictionDataFromResult(resultId).execute().body();
                        PredictionResult newPredictionResult = modelPredictionService.predict(resultData);

                        PredictionUploadRequest uploadRequest = new PredictionUploadRequest(
                                resultId,
                                predictionSummary.doctorId(),
                                newPredictionResult.confidence(),
                                newPredictionResult.prediction()
                        );
                        Prediction savedPrediction = predictionApi.uploadPrediction(uploadRequest).execute().body();

                        assert savedPrediction != null;

                        confidences.add(savedPrediction.confidence());
                        predictions.add(savedPrediction.prediction());

                } catch (Exception ex) {
                    log.error("Error processing resultId: {}", resultId, ex);
                }
            }

//            if (confidences.isEmpty()) {
//                throw new PredictionException("No predictions were processed for request ID: " + requestId);
//            }
//
//            double averageConfidence = countAverageConfidence(confidences);
//
//
//            updatePredictionRequestStatus(predictionSummary.id(), PredictionRequestStatus.COMPLETED, averageConfidence,
//                    predictions.getFirst()); //TODO: not first predict please
//
//        } catch (Exception e) {
//            log.error("Error during asynchronous prediction processing", e);
//            try {
//                //updatePredictionRequestStatus(predictionSummary.id(), PredictionRequestStatus.FAILED, null, null); //TODO: update to fail
//
//            } catch (Exception innerEx) {
//                log.error("Failed to update prediction summary to FAILED status", innerEx);
//            }
        }
    }

    private void updatePredictionRequestStatus(Long requestId, PredictionRequestStatus predictionRequestStatus,
                                               Double confidence, String prediction){
        try {
            PredictionSummaryUpdateRequest updateRequest = new PredictionSummaryUpdateRequest(
                    requestId,
                    predictionRequestStatus,
                    confidence,
                    prediction
            );
            Response<PredictionSummary> response = predictionApi.updatePredictionRequest(updateRequest).execute();

            if (!response.isSuccessful() || response.body() == null) {
                log.error("Failed to update prediction summary, response: {}", response.errorBody());
//                throw new PredictionException("Failed to update prediction summary: " + response.errorBody());
            }

            log.info("Prediction summary for request {} updated successfully.", requestId);
        } catch (Exception e) {
            String message = String.format("Failed to update prediction summary to %s status: %s", predictionRequestStatus.toString(), e.getMessage());
            log.error(message, e);
//            throw new PredictionException(message);
        }
    }

    private double countAverageConfidence(List<Double> confidences){
        return confidences.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }
}
