package com.bibliobytes.backend.users.dtos.confirmable;

import com.bibliobytes.backend.validation.Lowercase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateCredentialsDto implements Confirmable {
    private UUID id;
    @Email(message = "must be a valid email")
    @Lowercase(message = "must be in lowercase")
    private String email;
    @Email(message = "must be a valid email")
    @Lowercase(message = "must be in lowercase")
    private String newEmail;
    @Email(message = "must be a valid email")
    @Lowercase(message = "must be in lowercase")
    private String confirmNewEmail;
    @Size(min = 6, max = 25, message = "Password must be between 6 to 25 characters long.")
    private String password;
    @Size(min = 6, max = 25, message = "Password must be between 6 to 25 characters long.")
    private String newPassword;
    @Size(min = 6, max = 25, message = "Password must be between 6 to 25 characters long.")
    private String confirmNewPassword;
}
