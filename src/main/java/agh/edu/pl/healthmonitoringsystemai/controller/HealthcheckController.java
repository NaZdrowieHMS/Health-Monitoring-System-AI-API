package agh.edu.pl.healthmonitoringsystemai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/ai/health/status")
public class HealthcheckController {

    @GetMapping
    @Operation(
            summary = "Check if health of ai system is ready.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema =  @Schema())),
                    @ApiResponse(responseCode = "500", description = "Not OK",
                            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema =  @Schema())),
            },
            tags = {"Readiness Check"}
    )
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }
}
