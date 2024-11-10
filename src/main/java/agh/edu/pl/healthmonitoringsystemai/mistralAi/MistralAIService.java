package agh.edu.pl.healthmonitoringsystemai.mistralAi;

import agh.edu.pl.healthmonitoringsystem.request.AiFormAnalysisRequest;
import agh.edu.pl.healthmonitoringsystem.response.AiFormAnalysis;
import agh.edu.pl.healthmonitoringsystem.response.Form;
import agh.edu.pl.healthmonitoringsystemai.exception.MistralApiException;
import agh.edu.pl.healthmonitoringsystemai.util.JsonSanitizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MistralAIService {

    private final MistralAiChatModel chatModel;
    private final PromptCreator promptCreator;
    private final FormService formService;
    private final BeanOutputParser<AiAnalysisOutput> outputParser;

    @Autowired
    public MistralAIService(MistralAiChatModel chatModel, PromptCreator promptCreator, FormService formService) {
        this.chatModel = chatModel;
        this.promptCreator = promptCreator;
        this.formService = formService;
        this.outputParser = new BeanOutputParser<>(AiAnalysisOutput.class);
    }

    public AiFormAnalysis getAiAnalysisBasedOnForm(Long formId) {
        Form form = formService.retrieveFormById(formId);
        Prompt prompt = createPrompt(form);
        ChatResponse chatResponse = executeChatRequest(prompt);
        AiAnalysisOutput aiOutput = parseChatResponse(chatResponse);
        AiFormAnalysisRequest request = createRequest(aiOutput, form);
        return formService.saveFormAnalysis(request);
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
        return outputParser.parse(jsonContent);
    }

    private AiFormAnalysisRequest createRequest(AiAnalysisOutput output, Form form) {
        return AiFormAnalysisRequest.builder()
                .formId(form.id())
                .patientId(form.patientId())
                .diagnoses(output.diagnoses())
                .recommendations(output.recommendations())
                .build();
    }
}