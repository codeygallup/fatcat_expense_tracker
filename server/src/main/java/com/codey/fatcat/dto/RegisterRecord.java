package com.codey.fatcat.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRecord(
    @Email @NotBlank String email,
    @NotBlank String password
) {
}
