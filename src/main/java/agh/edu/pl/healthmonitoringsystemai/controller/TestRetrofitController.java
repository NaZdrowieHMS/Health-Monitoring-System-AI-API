package agh.edu.pl.healthmonitoringsystemai.controller;

import agh.edu.pl.healthmonitoringsystem.client.PatientApi;
import agh.edu.pl.healthmonitoringsystem.response.Result;
import agh.edu.pl.healthmonitoringsystemai.client.RetrofitClient;
import agh.edu.pl.healthmonitoringsystemai.exception.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "/api/test")
public class TestRetrofitController {

    private final PatientApi patientApi;

    public TestRetrofitController(RetrofitClient retrofitClient) {
        this.patientApi = retrofitClient.getRetrofitClient().create(PatientApi.class);
    }


    @Operation(
            summary = "Retrofit test result",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation",
                            content = @Content(schema = @Schema(type = "array", implementation = Result.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =  @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Server error",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =  @Schema(implementation = ErrorResponse.class))),
            },
            tags = {"Retrofit"}
    )
    @GetMapping("/retrofit/{patientId}")
    public ResponseEntity<List<Result>> testRetrofit(@PathVariable Long patientId) throws IOException {
        Response<List<Result>> result = patientApi.getPatientResults(patientId).execute();
        return ResponseEntity.ok(result.body());
    }
}
