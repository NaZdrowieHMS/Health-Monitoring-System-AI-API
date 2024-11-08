package agh.edu.pl.healthmonitoringsystemai.controller;

import agh.edu.pl.healthmonitoringsystem.response.Form;
import agh.edu.pl.healthmonitoringsystemai.mistralAi.model.MistralApiResponse;
import agh.edu.pl.healthmonitoringsystemai.mistralAi.service.MistralAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forms")
public class HealthFormController {

    private final MistralAIService mistralService;

    @Autowired
    public HealthFormController(MistralAIService mistralService) {
        this.mistralService = mistralService;
    }

    @PostMapping("/diagnosis")
    public MistralApiResponse getDiagnosis(@RequestBody Form healthForm) {
        return mistralService.getDiagnosisAndRecommendations(healthForm);
    }
}

