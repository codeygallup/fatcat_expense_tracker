package com.codey.fatcat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class CustomRestExceptionHandler {

  @ExceptionHandler
  public ResponseEntity<CustomErrorResponse> handleException(ResourceNotFoundException exc) {
    CustomErrorResponse errorResponse = new CustomErrorResponse();

    errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
    errorResponse.setMessage(exc.getMessage());
    errorResponse.setTimestamp(LocalDateTime.now());

    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler
  public ResponseEntity<CustomErrorResponse> handleException(Exception exc) {
    CustomErrorResponse errorResponse = new CustomErrorResponse();

    errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
    errorResponse.setMessage(exc.getMessage());
    errorResponse.setTimestamp(LocalDateTime.now());

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }
}
