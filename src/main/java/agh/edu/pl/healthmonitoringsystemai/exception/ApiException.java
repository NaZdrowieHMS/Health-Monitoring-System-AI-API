package agh.edu.pl.healthmonitoringsystemai.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final int statusCode;

    public ApiException(String message) {
        super(String.format("Exception caused by HMS API. %s", message));
        this.statusCode = 500;
    }

    public ApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
