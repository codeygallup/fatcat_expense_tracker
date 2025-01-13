package com.codey.fatcat.config;

import com.codey.fatcat.exception.CustomErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class SecurityErrorResponseHandler {
  private final ObjectMapper mapper;

  public SecurityErrorResponseHandler() {
    this.mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
  }

  public CustomErrorResponse buildErrorResponse(HttpStatus status, String message, String path) {
    return new CustomErrorResponse(
        status.value(),
        message,
        status == HttpStatus.UNAUTHORIZED ? "Unauthorized" : "Access denied",
        LocalDateTime.now(),
        path
    );
  }

  public void writeResponse(HttpServletResponse response, CustomErrorResponse errorResponse) throws IOException {
    response.setStatus(errorResponse.getStatus());
    response.setContentType("application/json");
    mapper.writeValue(response.getOutputStream(), errorResponse);
  }
}
