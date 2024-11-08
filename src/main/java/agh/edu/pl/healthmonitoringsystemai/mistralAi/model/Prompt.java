package agh.edu.pl.healthmonitoringsystemai.mistralAi.model;

import jakarta.validation.constraints.NotBlank;

public record Prompt (
        @NotBlank String role,
        @NotBlank String content
) {}

