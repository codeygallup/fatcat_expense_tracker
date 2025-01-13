package com.codey.fatcat.config;

import com.codey.fatcat.exception.CustomErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final SecurityErrorResponseHandler errorResponseHandler;

  public CustomAuthenticationEntryPoint(SecurityErrorResponseHandler errorResponseHandler) {
    this.errorResponseHandler = errorResponseHandler;
  }

  @Override
  public void commence(HttpServletRequest request,
                       HttpServletResponse response,
                       AuthenticationException authException) throws IOException {
    CustomErrorResponse errorResponse = errorResponseHandler.buildErrorResponse(
        HttpStatus.UNAUTHORIZED,
        "Authentication required to access this resource",
        request.getRequestURI()
    );
    errorResponseHandler.writeResponse(response, errorResponse);
  }
}
