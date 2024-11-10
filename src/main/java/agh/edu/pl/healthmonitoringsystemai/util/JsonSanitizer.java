package agh.edu.pl.healthmonitoringsystemai.util;


import org.springframework.stereotype.Component;

@Component
public class JsonSanitizer {
    public static String sanitize(String jsonContent) {
        return jsonContent.trim().replaceAll("^```json\\s*|```$", "").trim();
    }
}
