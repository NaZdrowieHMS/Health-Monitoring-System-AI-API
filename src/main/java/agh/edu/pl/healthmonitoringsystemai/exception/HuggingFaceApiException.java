package agh.edu.pl.healthmonitoringsystemai.exception;

public class HuggingFaceApiException extends RuntimeException {
    public HuggingFaceApiException(String message) {
        super(String.format("Exception caused by Hugging Face API. %s", message));
    }
}
