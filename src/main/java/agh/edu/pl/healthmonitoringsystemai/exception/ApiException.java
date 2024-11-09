package agh.edu.pl.healthmonitoringsystemai.exception;


public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(String.format("Exception caused by HMS API. %s", message));
    }
}
