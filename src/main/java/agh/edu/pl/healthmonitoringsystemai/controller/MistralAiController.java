package agh.edu.pl.healthmonitoringsystemai.controller;

import agh.edu.pl.healthmonitoringsystemai.exception.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import agh.edu.pl.healthmonitoringsystemai.mistralAi.MistralAIService;


@RestController
@RequestMapping(path = "/api/ai/report")
public class MistralAiController {

    private final MistralAIService mistralService;

    public MistralAiController(MistralAIService mistralService) {
        this.mistralService = mistralService;
    }

    @GetMapping("/{formId}")
    @Operation(
            summary = "Get Ai health report based on a specific form.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation",
//                            content = @Content(schema = @Schema(implementation = AiReport.class))),
                            content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "404", description = "Not found",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Server error",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =  @Schema(implementation = ErrorResponse.class))),
            },
            tags = {"Mistral AI"}
    )
    public ResponseEntity<String> getAiReportBasedOnForm(@Parameter(description = "Form ID") @PathVariable("formId") Long formId) {

//        AiReport aiHealthReport = aiReportService.getAiReportBasedOnForm(formId);
//        return ResponseEntity.ok(aiHealthReport);
        String aiHealthReport = mistralService.getAiReportBasedOnForm(formId);
        return ResponseEntity.ok(aiHealthReport);
    }
}