package agh.edu.pl.healthmonitoringsystemai.mistralAi.model;

public record MistralApiResponse(
        String id,
        String object,
        long created,
        String model,
        MistralChoice[] choices
) {

    public record MistralChoice(
            Message message,
            int index,
            String finish_reason
    ) {}

    public record Message(
            String role,
            String content
    ) {}
}
