package com.bibliobytes.backend.users.requests;

import com.bibliobytes.backend.validation.validpassword.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ValidatePasswordRequest {
    @NotBlank(message = "Password is required")
    @ValidPassword(message = "Incorrect Password")
    private String password;
}
