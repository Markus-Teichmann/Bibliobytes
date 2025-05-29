package com.bibliobytes.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConfirmCodeRequest {
    @NotBlank(message = "code is required")
    private String code;
}
