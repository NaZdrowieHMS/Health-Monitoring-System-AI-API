package agh.edu.pl.healthmonitoringsystemai.mistralAi.service;

import agh.edu.pl.healthmonitoringsystem.response.Form;
import agh.edu.pl.healthmonitoringsystemai.client.MistralApiClient;
import agh.edu.pl.healthmonitoringsystemai.mistralAi.model.MistralApiResponse;
import agh.edu.pl.healthmonitoringsystemai.mistralAi.component.PromptGenerator;
import agh.edu.pl.healthmonitoringsystemai.exception.MistralApiException;
import agh.edu.pl.healthmonitoringsystemai.mistralAi.model.MistralApiRequest;
import agh.edu.pl.healthmonitoringsystemai.mistralAi.model.Prompt;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class MistralAIService {

    private final PromptGenerator promptGenerator;
    private final MistralApiClient mistralApiClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public MistralAIService(MistralApiClient mistralApiClient, PromptGenerator promptGenerator, ObjectMapper objectMapper) {
        this.mistralApiClient = mistralApiClient;
        this.promptGenerator = promptGenerator;
        this.objectMapper = objectMapper;
    }

    public MistralApiResponse getDiagnosisAndRecommendations(Form healthForm) {
        Prompt prompt = promptGenerator.generate(healthForm);
        return getMistralResponse(prompt);
    }

    public MistralApiResponse getMistralResponse(Prompt prompt) {
        MistralApiRequest request = new MistralApiRequest(prompt);
        try {
            String jsonBody = objectMapper.writeValueAsString(request);
            ResponseEntity<MistralApiResponse> response = mistralApiClient.sendRequest(jsonBody);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error executing Mistral API request", e);
            throw new MistralApiException(e.getMessage());
        }
    }
}