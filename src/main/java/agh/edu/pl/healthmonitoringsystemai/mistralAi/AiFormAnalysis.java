package agh.edu.pl.healthmonitoringsystemai.mistralAi;

import java.util.List;

public record AiFormAnalysis(
        Long patientId,
        Long formId,
        List<String> diagnoses,
        List<String> recommendations
) {}
