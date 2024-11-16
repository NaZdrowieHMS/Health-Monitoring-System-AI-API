package agh.edu.pl.healthmonitoringsystemai.breastCancerPredictionAi;

import java.time.LocalDateTime;

public record Prediction (
        Long id,
        Long resultId,
        String prediction,
        Double confidence,
        LocalDateTime createdDate )
{}