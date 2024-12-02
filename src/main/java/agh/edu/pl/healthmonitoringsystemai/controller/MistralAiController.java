package agh.edu.pl.healthmonitoringsystemai.controller;

import agh.edu.pl.healthmonitoringsystem.response.AiFormAnalysis;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import agh.edu.pl.healthmonitoringsystemai.mistralAi.MistralAIService;


@RestController
@RequestMapping(path = "/api/ai/forms/")
public class MistralAiController {

    private final MistralAIService mistralAiService;

    public MistralAiController(MistralAIService mistralAiService) {
        this.mistralAiService = mistralAiService;
    }

    @GetMapping("/{formId}/analysis")
    @Operation(
            summary = "Get Ai health report based on a specific form.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation",
                            content = @Content(schema = @Schema(implementation = AiFormAnalysis.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid Request",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Not found",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Server error",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =  @Schema(implementation = ErrorResponse.class))),
            },
            tags = {"AI Form Analysis"}
    )
    public ResponseEntity<AiFormAnalysis> getAiAnalysisBasedOnForm(@Parameter(description = "Form ID") @PathVariable("formId") Long formId,
                                                                   @Parameter(description = "User ID") @RequestHeader(name = "userId") Long userId) {

        AiFormAnalysis aiFormAnalysis = mistralAiService.getAiAnalysisBasedOnForm(formId, userId);
        return ResponseEntity.ok(aiFormAnalysis);
    }
}