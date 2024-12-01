package agh.edu.pl.healthmonitoringsystemai.exception;

public class AwsApiException extends RuntimeException {
    public AwsApiException(String message) {
        super(String.format("Exception caused by AWS API Client. %s", message));
    }
}
