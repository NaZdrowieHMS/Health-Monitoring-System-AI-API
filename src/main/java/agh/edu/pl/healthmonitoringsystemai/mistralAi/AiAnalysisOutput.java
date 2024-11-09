package agh.edu.pl.healthmonitoringsystemai.mistralAi;

import java.util.List;

public record AiAnalysisOutput(
        List<String> diagnoses,
        List<String> recommendations
) {
    public AiAnalysisOutput {
        validate(diagnoses, recommendations);
    }

    private void validate(List<String> diagnoses, List<String> recommendations) {
        if (diagnoses.size() > 5) {
            throw new IllegalArgumentException("Max 5 diagnoses allowed.");
        }
        if (recommendations.size() > 5) {
            throw new IllegalArgumentException("Max 5 recommendations allowed.");
        }
    }
}
