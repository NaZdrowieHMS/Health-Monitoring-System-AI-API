package agh.edu.pl.healthmonitoringsystemai.mistralAi;

import agh.edu.pl.healthmonitoringsystem.response.Form;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PromptCreator {

    private static final String MESSAGE_TEMPLATE = "Wciel się w role lekarza. Z użyciem języka polskiego podaj możliwe diagnozy i zalecenia które" +
            " mógłby zasugerować lekarz na podstawie następujących objawów zdrowotnych pacjenta: \\n{patientData}. " +
            "Dla każdego z parametrów podaj maksymalnie 5 elementów, a minimalnmie 2 elementy {format}";

    public Prompt create(Form healthForm, String format) {
        log.info("Generating prompt");

        String patientData = healthForm.content().stream()
                .map(entry -> entry.key() + ": " + entry.value())
                .collect(Collectors.joining("\n"));

        PromptTemplate template = new PromptTemplate(MESSAGE_TEMPLATE, Map.of("patientData", patientData, "format", format));

        return template.create();
    }
}
