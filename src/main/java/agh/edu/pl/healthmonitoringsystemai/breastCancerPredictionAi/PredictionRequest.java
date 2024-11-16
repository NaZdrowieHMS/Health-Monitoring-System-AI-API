package agh.edu.pl.healthmonitoringsystemai.breastCancerPredictionAi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PredictionRequest {

    @NotBlank(message = "Image in Base64 format is required")
    private String imageBase64;

    @JsonCreator
    public PredictionRequest(@JsonProperty("imageBase64") String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}
