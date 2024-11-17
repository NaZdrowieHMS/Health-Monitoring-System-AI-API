package agh.edu.pl.healthmonitoringsystemai.controller;

import agh.edu.pl.healthmonitoringsystemai.exception.ApiException;
import agh.edu.pl.healthmonitoringsystemai.exception.HuggingFaceApiException;
import agh.edu.pl.healthmonitoringsystemai.exception.InvalidImageException;
import agh.edu.pl.healthmonitoringsystemai.exception.MistralApiException;
import agh.edu.pl.healthmonitoringsystemai.exception.PredictionException;
import agh.edu.pl.healthmonitoringsystemai.exception.ResourceNotFoundException;
import agh.edu.pl.healthmonitoringsystemai.exception.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("A ResourceNotFoundException occurred: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidImageException.class)
    public ResponseEntity<ErrorResponse> handleInvalidImageException(InvalidImageException ex) {
        log.error("A InvalidImageException occurred: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MistralApiException.class)
    public final ResponseEntity<ErrorResponse> handleMistralApiException(MistralApiException ex) {
        log.error("A MistralApiException occurred: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HuggingFaceApiException.class)
    public final ResponseEntity<ErrorResponse> handleHuggingFaceApiException(HuggingFaceApiException ex) {
        log.error("A HuggingFaceApiException occurred: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(PredictionException.class)
    public final ResponseEntity<ErrorResponse> handlePredictionException(PredictionException ex) {
        log.error("A PredictionException occurred: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(ApiException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", ex.getStatusCode());
        response.put("message", ex.getMessage());
        log.error("A ApiException occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(ex.getStatusCode()).body(response);
    }
}