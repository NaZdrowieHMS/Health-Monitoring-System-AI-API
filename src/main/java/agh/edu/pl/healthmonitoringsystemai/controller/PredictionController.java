package agh.edu.pl.healthmonitoringsystemai.controller;

import agh.edu.pl.healthmonitoringsystem.request.PredictionSummaryRequest;
import agh.edu.pl.healthmonitoringsystem.response.PredictionSummary;
import agh.edu.pl.healthmonitoringsystemai.breastCancerPredictionAi.PredictionRequestService;
import agh.edu.pl.healthmonitoringsystemai.breastCancerPredictionAi.RequestResponse;
import agh.edu.pl.healthmonitoringsystemai.exception.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/ai/predictions")
@CrossOrigin
public class PredictionController {
    private final PredictionRequestService predictionRequestService;

    public PredictionController(PredictionRequestService predictionRequestService) {
        this.predictionRequestService = predictionRequestService;
    }

    @PostMapping
    @Operation(
            summary = "Predict breast cancer possibility with AI model.",
            responses = {
                    @ApiResponse(responseCode = "202", description = "Request accepted for processing",
                            content = @Content(schema = @Schema(implementation = RequestResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Server error",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            },
            tags = {"AI Prediction"}
    )
    public ResponseEntity<RequestResponse> createPrediction(@Valid @RequestBody PredictionSummaryRequest request) {

        RequestResponse response = predictionRequestService.createPredictionRequest(request);
        return ResponseEntity.accepted().body(response);
    }


    @GetMapping("/{requestId}")
    @Operation(
            summary = "Check prediction status and result.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Operation successful",
                            content = @Content(schema = @Schema(implementation = PredictionSummary.class))),
                    @ApiResponse(responseCode = "404", description = "Prediction not found",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Server error",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            },
            tags = {"AI Prediction"}
    )
    public ResponseEntity<PredictionSummary> getPredictionStatus(@PathVariable Long requestId) {
        PredictionSummary summary = predictionRequestService.getPredictionStatus(requestId);
        return ResponseEntity.ok(summary);
    }
}