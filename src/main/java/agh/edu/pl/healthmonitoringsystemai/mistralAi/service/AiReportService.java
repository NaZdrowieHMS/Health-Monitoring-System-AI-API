package agh.edu.pl.healthmonitoringsystemai.mistralAi.service;

import agh.edu.pl.healthmonitoringsystem.client.FormApi;
import agh.edu.pl.healthmonitoringsystem.response.Form;
import agh.edu.pl.healthmonitoringsystemai.client.RetrofitClient;
import agh.edu.pl.healthmonitoringsystemai.mistralAi.model.AiReport;
import org.springframework.stereotype.Service;
import retrofit2.Response;


@Service
public class AiReportService {
    private final FormApi formApi;

    public AiReportService(RetrofitClient retrofitClient) {
        this.formApi = retrofitClient.getRetrofitClient().create(FormApi.class);
    }

    public AiReport getAiReportBasedOnForm(Long formId) {
        try {
            Response<Form> form = formApi.getFormById(formId).execute();
            System.out.println(form.body());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        return null;
    }
}
