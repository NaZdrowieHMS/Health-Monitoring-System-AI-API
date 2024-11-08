package agh.edu.pl.healthmonitoringsystemai.exception;

public class MistralApiException extends RuntimeException {
    public MistralApiException(String message) {
        super(String.format("Exception caused by Mistral API. %s", message));
    }
}

