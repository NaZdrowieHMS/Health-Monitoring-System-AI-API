package agh.edu.pl.healthmonitoringsystemai.mistralAi;

import agh.edu.pl.healthmonitoringsystem.model.FormAiAnalysis;
import agh.edu.pl.healthmonitoringsystem.request.AiFormAnalysisRequest;
import agh.edu.pl.healthmonitoringsystem.response.Form;
import agh.edu.pl.healthmonitoringsystemai.exception.MistralApiException;
import agh.edu.pl.healthmonitoringsystemai.util.JsonSanitizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.ai.mistralai.api.MistralAiApi;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MistralAIService {

    @Value("${spring.ai.mistralai.api-key}")
    private String apiKey;

    private MistralAiChatModel chatModel;
    private final PromptCreator promptCreator;
    private final FormService formService;
    private final BeanOutputParser<AiAnalysisOutput> outputParser;

    @Autowired
    public MistralAIService(PromptCreator promptCreator, FormService formService) {
        this.promptCreator = promptCreator;
        this.formService = formService;
        this.outputParser = new BeanOutputParser<>(AiAnalysisOutput.class);
    }

    @PostConstruct
    public void init() {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new MistralApiException("Mistral API key is missing");
        }
        this.chatModel = new MistralAiChatModel(new MistralAiApi(apiKey));
    }

    public FormAiAnalysis getAiAnalysisBasedOnForm(Long patientId, Long userId) {
        Form form = formService.retrieveLatestForm(patientId, userId);
        if(form == null) return null;
        Prompt prompt = createPrompt(form);
        ChatResponse chatResponse = executeChatRequest(prompt);
        AiAnalysisOutput aiOutput = parseChatResponse(chatResponse);
        return new FormAiAnalysis(form.id(), aiOutput.diagnoses(), aiOutput.recommendations(), form.createDate());
    }

    private Prompt createPrompt(Form form) {
        return promptCreator.create(form, outputParser.getFormat());
    }

    private ChatResponse executeChatRequest(Prompt prompt) {
        log.info("Executing MistralAI service with prompt: {}", prompt);
        try {
            return chatModel.call(prompt);
        } catch (Exception e) {
            log.error("Error executing Mistral API request", e);
            throw new MistralApiException("API execution error: " + e.getMessage());
        }
    }

    private AiAnalysisOutput parseChatResponse(ChatResponse chatResponse) {
        String jsonContent = JsonSanitizer.sanitize(chatResponse.getResult().getOutput().getContent());
        log.info("Sanitized JSON content: {}", jsonContent);

        ObjectMapper mapper = new ObjectMapper();
        try {
            AiAnalysisOutput output = mapper.readValue(jsonContent, AiAnalysisOutput.class);
            return output;
        } catch (Exception e) {
            throw new MistralApiException("Error during Mistral API response deserialization: " + e.getMessage());
        }
    }

    private AiFormAnalysisRequest createRequest(AiAnalysisOutput output, Form form, Long userId) {
        return AiFormAnalysisRequest.builder()
                .formId(form.id())
                .patientId(form.patientId())
                .doctorId(userId)
                .diagnoses(output.diagnoses())
                .recommendations(output.recommendations())
                .build();
    }
}