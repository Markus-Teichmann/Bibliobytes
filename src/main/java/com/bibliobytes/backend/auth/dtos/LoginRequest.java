package com.bibliobytes.backend.auth.dtos;

import com.bibliobytes.backend.validation.Lowercase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    @Email(message = "must be a valid email")
    @Lowercase(message = "must be in lowercase")
    String email;
    @NotBlank
    @Size(min = 6, max = 25, message = "Invalid Password.")
    String password;
}
