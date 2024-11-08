package agh.edu.pl.healthmonitoringsystemai.mistralAi.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MistralApiRequest {
    private String model = "mistral-small-latest";
    private double temperature = 1.0;
    private double top_p = 1.0;
    private int max_tokens = 150;
    private boolean stream = false;
    private String stop = "default_stop_value";
    private int random_seed = 0;
    @NotNull
    private List<Prompt> messages;

    public MistralApiRequest(Prompt prompt) {
        this.messages = List.of(prompt);
    }

    public MistralApiRequest(List<Prompt> prompts) {
        this.messages = prompts;
    }
}
