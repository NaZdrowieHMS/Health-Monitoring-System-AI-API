package agh.edu.pl.healthmonitoringsystemai.exception;

public class PredictionException extends RuntimeException {
    public PredictionException(String message) {
        super(String.format("Exception occurred during breast cancer prediction. %s", message));
    }
}
