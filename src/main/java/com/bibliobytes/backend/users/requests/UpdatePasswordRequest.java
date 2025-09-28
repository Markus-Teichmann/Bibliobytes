package com.bibliobytes.backend.users.requests;

import com.bibliobytes.backend.validation.passwordsmatch.PasswordRequest;
import com.bibliobytes.backend.validation.passwordsmatch.PasswordsMatch;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@PasswordsMatch(message = "Passwords do not match.")
@Data
public class UpdatePasswordRequest implements PasswordRequest, Serializable {
    @NotBlank(message = "Current Password is required")
    @Size(min = 6, max = 25, message = "Password must be between 6 to 25 characters long.")
    private String oldPassword;
    @Size(min = 6, max = 25, message = "Password must be between 6 to 25 characters long.")
    private String newPassword;
    @Size(min = 6, max = 25, message = "Password must be between 6 to 25 characters long.")
    private String confirmNewPassword;
}
