package com.codey.fatcat.config;

import com.codey.fatcat.exception.CustomErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
  private final SecurityErrorResponseHandler errorResponseHandler;

  public CustomAccessDeniedHandler(SecurityErrorResponseHandler errorResponseHandler) {
    this.errorResponseHandler = errorResponseHandler;
  }

  @Override
  public void handle(HttpServletRequest request,
                     HttpServletResponse response,
                     AccessDeniedException accessDeniedException) throws IOException {
    CustomErrorResponse errorResponse = errorResponseHandler.buildErrorResponse(
        HttpStatus.FORBIDDEN,
        "You don't have permission to access this record",
        request.getRequestURI()
    );
    errorResponseHandler.writeResponse(response, errorResponse);
  }
}
