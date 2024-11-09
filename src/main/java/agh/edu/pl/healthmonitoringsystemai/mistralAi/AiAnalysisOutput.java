package agh.edu.pl.healthmonitoringsystemai.mistralAi;

import java.util.List;

public record AiAnalysisOutput(
        List<String> diagnoses,
        List<String> recommendations
) {}
