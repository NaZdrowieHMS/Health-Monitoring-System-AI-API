package agh.edu.pl.healthmonitoringsystemai.mistralAi;

import agh.edu.pl.healthmonitoringsystem.client.FormApi;
import agh.edu.pl.healthmonitoringsystem.response.Form;
import agh.edu.pl.healthmonitoringsystemai.client.RetrofitClient;
import agh.edu.pl.healthmonitoringsystemai.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.util.List;

@Slf4j
@Service
public class FormService {
    private final FormApi formApi;

    @Autowired
    public FormService(RetrofitClient retrofitClient) {
        this.formApi = retrofitClient.getRetrofitClient().create(FormApi.class);
    }

    public Form retrieveLatestForm(Long patientId, Long doctorId) {
        log.info("Retrieving latest form");
        try {
            Response<List<Form>> response = formApi.getAllHealthForms(0, 1, doctorId, patientId).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body().stream().findFirst().orElse(null);
            }
            return null;
        } catch (Exception e) {
            throw new ResourceNotFoundException("Error fetching latest form for patient " + patientId + ": " + e.getMessage());
        }
    }
}
