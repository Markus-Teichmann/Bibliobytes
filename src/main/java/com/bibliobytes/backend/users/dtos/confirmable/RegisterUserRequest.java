package com.bibliobytes.backend.users.dtos.confirmable;

import com.bibliobytes.backend.validation.Lowercase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequest implements Confirmable {
    @NotBlank(message = "email is required")
    @Email(message = "must be a valid email")
    @Lowercase(message = "must be in lowercase")
    private String email;
    @NotBlank(message = "first name is required")
    private String firstName;
    @NotBlank(message = "last name is required")
    private String lastName;
    @Size(min = 6, max = 25, message = "Password must be between 6 to 25 characters long.")
    private String password;
}
