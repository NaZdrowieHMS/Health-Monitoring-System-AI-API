package agh.edu.pl.healthmonitoringsystemai.mistralAi;

import agh.edu.pl.healthmonitoringsystem.response.Form;
import agh.edu.pl.healthmonitoringsystem.response.FormEntry;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Component;

@Component
public class PromptGenerator {


    public Prompt generate(Form healthForm) {
        StringBuilder message = new StringBuilder("Na podstawie następujących objawów zdrowotnych pacjenta: \n");

        for (FormEntry field : healthForm.content()) {
            message.append(field.key()).append(": ").append(field.value()).append("\n");
        }

        message.append("Podaj możliwą diagnozę i zalecenia.");
//
//        PromptTemplate template = new PromptTemplate(message.toString());
//        Prompt prompt = template.create();
        Prompt prompt = new Prompt(String.valueOf(message));

        return prompt;
    }

}

