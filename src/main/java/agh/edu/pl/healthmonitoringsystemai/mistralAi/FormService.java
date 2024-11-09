package agh.edu.pl.healthmonitoringsystemai.mistralAi;

import agh.edu.pl.healthmonitoringsystem.client.FormApi;
import agh.edu.pl.healthmonitoringsystem.response.Form;
import agh.edu.pl.healthmonitoringsystemai.client.RetrofitClient;
import agh.edu.pl.healthmonitoringsystemai.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Response;

@Slf4j
@Service
public class FormService {
    private final FormApi formApi;

    @Autowired
    public FormService(RetrofitClient retrofitClient) {
        this.formApi = retrofitClient.getRetrofitClient().create(FormApi.class);
    }

    public Form retrieveFormById(Long formId) {
        log.info("Retrieving form by id: {}", formId);
        try {
            Response<Form> response = formApi.getFormById(formId).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
            throw new ResourceNotFoundException("Form with ID " + formId + " not found.");
        } catch (Exception e) {
            throw new ResourceNotFoundException("Error fetching form with ID " + formId + ": " + e.getMessage());
        }
    }
}
