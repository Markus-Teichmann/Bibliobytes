package com.bibliobytes.backend.users.requests;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterCodeRequest {
    @NotBlank(message = "code is required")
    @Size(min = 6, max = 6, message = "Code must be 6 digits long.")
    @Digits(message = "Code must contain digits.", integer = 6, fraction = 0)
    private String code;
}
