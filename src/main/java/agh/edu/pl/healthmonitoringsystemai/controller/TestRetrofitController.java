package agh.edu.pl.healthmonitoringsystemai.controller;

import agh.edu.pl.healthmonitoringsystem.client.PatientApi;
import agh.edu.pl.healthmonitoringsystem.response.Result;
import agh.edu.pl.healthmonitoringsystemai.client.RetrofitClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "/test")
public class TestRetrofitController {
    RetrofitClient retrofitClient = new RetrofitClient();
    PatientApi patientApi = retrofitClient.getRetrofitClient().create(PatientApi .class);

    @GetMapping("/retrofit/{patientId}")
    public ResponseEntity<List<Result>> testRetrofit(@PathVariable Long patientId) throws IOException {
        Response<List<Result>> result = patientApi.getPatientResults(3L).execute();
        return ResponseEntity.ok(result.body());
    }
}
