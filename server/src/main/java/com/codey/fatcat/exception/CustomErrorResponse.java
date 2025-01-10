package com.codey.fatcat.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CustomErrorResponse {

  private int status;
  private String message;
  private LocalDateTime timestamp;
}
