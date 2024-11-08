package agh.edu.pl.healthmonitoringsystemai.client;

import agh.edu.pl.healthmonitoringsystemai.mistralAi.model.MistralApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Component
public class MistralApiClient {

    private final String AI_API_URL;
    private final String AI_API_TOKEN;
    private final RestTemplate restTemplate;


    public MistralApiClient(@Value("${ai.api.base-url}") String aiApiBaseUrl, @Value("${ai.api.token}") String aiApiToken,
                            RestTemplate restTemplate) {
        this.AI_API_URL = aiApiBaseUrl + "/v1/chat/completions";
        this.AI_API_TOKEN = aiApiToken;
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<MistralApiResponse> sendRequest(String jsonBody) {
        HttpHeaders headers = createHeaders();
        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
        return restTemplate.exchange(AI_API_URL, HttpMethod.POST, entity, MistralApiResponse.class);
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + AI_API_TOKEN);
        headers.set("Content-Type", "application/json");
        return headers;
    }
}
