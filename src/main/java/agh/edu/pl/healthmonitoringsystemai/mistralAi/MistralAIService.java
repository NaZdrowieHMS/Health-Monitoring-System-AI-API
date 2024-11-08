package agh.edu.pl.healthmonitoringsystemai.mistralAi;

import agh.edu.pl.healthmonitoringsystem.client.FormApi;
import agh.edu.pl.healthmonitoringsystem.response.Form;
import agh.edu.pl.healthmonitoringsystemai.client.RetrofitClient;
import agh.edu.pl.healthmonitoringsystemai.exception.ResourceNotFoundException;
import agh.edu.pl.healthmonitoringsystemai.exception.MistralApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Response;


@Slf4j
@Service
public class MistralAIService {
    private final MistralAiChatModel chatModel;
    private final PromptGenerator promptGenerator;
    private final FormApi formApi;

    @Autowired
    public MistralAIService(RetrofitClient retrofitClient, MistralAiChatModel chatModel, PromptGenerator promptGenerator) {
        this.formApi = retrofitClient.getRetrofitClient().create(FormApi.class);
        this.chatModel = chatModel;
        this.promptGenerator = promptGenerator;
    }

    public String getAiReportBasedOnForm(Long formId) {
        Form form = fetchForm(formId);
        Prompt prompt = promptGenerator.generate(form);
        return execute(prompt);
    }

    private String execute(Prompt prompt) {
        try {
            return chatModel.call(prompt).getResult().getOutput().getContent();
        } catch (Exception e) {
            log.error("Error executing Mistral API request", e);
            throw new MistralApiException(e.getMessage());
        }
    }

    private Form fetchForm(Long formId){
        try {
            Response<Form> form = formApi.getFormById(formId).execute();
            return form.body();
        } catch (Exception e) {
            throw new ResourceNotFoundException(String.format("Exception while fetching form with id + %s.", formId));
        }
    }
}