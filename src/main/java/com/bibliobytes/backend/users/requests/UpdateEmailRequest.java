package com.bibliobytes.backend.users.requests;

import com.bibliobytes.backend.validation.emailsmatch.EmailRequest;
import com.bibliobytes.backend.validation.emailsmatch.EmailsMatch;
import com.bibliobytes.backend.validation.lowercase.Lowercase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@EmailsMatch(message = "Emails do not match.")
@Data
public class UpdateEmailRequest implements EmailRequest {
    @NotBlank(message = "Current Email required")
    @Email(message = "must be a valid email")
    @Lowercase(message = "must be in lowercase")
    private String oldEmail;
    @NotBlank(message = "New Email required")
    @Email(message = "must be a valid email")
    @Lowercase(message = "must be in lowercase")
    private String newEmail;
    @NotBlank(message = "Please Confirm new Email")
    @Email(message = "must be a valid email")
    @Lowercase(message = "must be in lowercase")
    private String confirmNewEmail;
}
