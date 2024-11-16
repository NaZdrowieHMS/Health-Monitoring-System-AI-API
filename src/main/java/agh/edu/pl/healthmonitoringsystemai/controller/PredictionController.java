//package agh.edu.pl.healthmonitoringsystemai.controller;
//
//
//import agh.edu.pl.healthmonitoringsystemai.breastCancerPredictionAi.Prediction;
//import agh.edu.pl.healthmonitoringsystemai.breastCancerPredictionAi.PredictionRequest;
//import agh.edu.pl.healthmonitoringsystemai.breastCancerPredictionAi.PredictionService;
//import agh.edu.pl.healthmonitoringsystemai.exception.response.ErrorResponse;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import jakarta.validation.Valid;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//
//@RestController
//@RequestMapping("/api/predictions")
//@CrossOrigin
//public class PredictionController {
//    private final PredictionService predictionService;
//
//    public PredictionController(PredictionService predictionService) {
//        this.predictionService = predictionService;
//    }
//
//    @PostMapping(value = "/test", consumes = MediaType.APPLICATION_JSON_VALUE)
//    @Operation(
//            summary = "Predict breast cancer possibility with AI model.",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Prediction successful",
//                            content = @Content(schema = @Schema(implementation = Prediction.class))),
//                    @ApiResponse(responseCode = "400", description = "Invalid request",
//                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =  @Schema(implementation = ErrorResponse.class))),
//                    @ApiResponse(responseCode = "500", description = "Server error",
//                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =  @Schema(implementation = ErrorResponse.class))),
//            },
//            tags = {"AI Prediction"}
//    )
//    public ResponseEntity<Prediction> testPrediction(@Parameter(description = "Test prediction request")
//                                                     @Valid @RequestBody PredictionRequest request) {
//
//        predictionService.predict(request);
////        Prediction prediction = predictionService.predict(request);
//        return ResponseEntity.ok(null);
//    }
//}
package agh.edu.pl.healthmonitoringsystemai.controller;


import agh.edu.pl.healthmonitoringsystemai.breastCancerPredictionAi.Prediction;
import agh.edu.pl.healthmonitoringsystemai.breastCancerPredictionAi.PredictionRequest;
import agh.edu.pl.healthmonitoringsystemai.breastCancerPredictionAi.PredictionService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
@RequestMapping("/api/predictions")
@CrossOrigin
public class PredictionController {
    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @PostMapping(value = "/test", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Prediction> testPrediction(@Parameter(description = "Test prediction request")
                                                     @Valid @RequestBody PredictionRequest request) throws IOException {

        // TODO: change endpoint
        predictionService.predict(request);
//        predictionService.predictImage(file);

        return ResponseEntity.ok(null);
    }
}
