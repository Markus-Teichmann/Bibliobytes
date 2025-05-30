package com.bibliobytes.backend.dtos;

import com.bibliobytes.backend.validation.Lowercase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterProfileRequest {
    @NotBlank(message = "email is required")
    @Email(message = "must be a valid email")
    @Lowercase(message = "must be in lowercase")
    private String email;
    @NotBlank(message = "first name is required")
    private String firstName;
    @NotBlank(message = "last name is required")
    private String lastName;
}
