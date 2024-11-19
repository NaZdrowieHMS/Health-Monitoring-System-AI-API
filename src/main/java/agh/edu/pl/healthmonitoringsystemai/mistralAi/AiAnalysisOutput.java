package agh.edu.pl.healthmonitoringsystemai.mistralAi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record AiAnalysisOutput(
        List<String> diagnoses,
        List<String> recommendations
) {
    @JsonCreator
    public AiAnalysisOutput(
            @JsonProperty("diagnoses") List<String> diagnoses,
            @JsonProperty("recommendations") List<String> recommendations
    ) {
        this.diagnoses = diagnoses;
        this.recommendations = recommendations;
    }
}
