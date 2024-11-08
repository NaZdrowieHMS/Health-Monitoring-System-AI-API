package agh.edu.pl.healthmonitoringsystemai.mistralAi.component;

import agh.edu.pl.healthmonitoringsystem.response.Form;
import agh.edu.pl.healthmonitoringsystem.response.FormEntry;
import agh.edu.pl.healthmonitoringsystemai.mistralAi.model.Prompt;
import org.springframework.stereotype.Component;

@Component
public class PromptGenerator {

    public Prompt generate(Form healthForm) {
        StringBuilder prompt = new StringBuilder("Na podstawie następujących objawów zdrowotnych pacjenta: \n");

        for (FormEntry field : healthForm.content()) {
            prompt.append(field.key()).append(": ").append(field.value()).append("\n");
        }

        prompt.append("Podaj możliwą diagnozę i zalecenia.");

        return new Prompt("user", prompt.toString());
    }

}

