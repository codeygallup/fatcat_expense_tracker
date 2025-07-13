package com.codey.fatcat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<CustomErrorResponse> handleResourceNotFoundException(ResourceNotFoundException exc,
                                                                             WebRequest request) {
    CustomErrorResponse errorResponse = buildErrorResponse(
            HttpStatus.NOT_FOUND,
            exc.getMessage(),
            "Resource not found",
            request
    );

    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<CustomErrorResponse> handleUnauthorizedException(UnauthorizedException exc,
                                                                         WebRequest request) {
    CustomErrorResponse errorResponse = buildErrorResponse(
            HttpStatus.UNAUTHORIZED,
            exc.getMessage(),
            "Unauthorized buddy",
            request
    );

    return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<CustomErrorResponse> handleAccessDeniedException(AccessDeniedException exc,
                                                                         WebRequest request) {
    CustomErrorResponse errorResponse = buildErrorResponse(
            HttpStatus.FORBIDDEN,
            exc.getMessage(),
            "Access Denied",
            request
    );

    return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<CustomErrorResponse> handleGenericException(Exception exc, WebRequest request) {

    CustomErrorResponse errorResponse = buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            exc.getMessage(),
            "Bad request",
            request
    );

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  private CustomErrorResponse buildErrorResponse(HttpStatus status, String message, String error, WebRequest request) {
    CustomErrorResponse errorResponse = new CustomErrorResponse();

    errorResponse.setStatus(status.value());
    errorResponse.setMessage(message);
    errorResponse.setError(error);
    errorResponse.setTimestamp(LocalDateTime.now());
    errorResponse.setPath(((ServletWebRequest) request).getRequest().getRequestURI());
    return errorResponse;
  }
}
