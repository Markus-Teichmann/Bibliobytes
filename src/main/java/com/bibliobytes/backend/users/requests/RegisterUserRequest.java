package com.bibliobytes.backend.users.requests;

import com.bibliobytes.backend.validation.lowercase.Lowercase;
import com.bibliobytes.backend.validation.notTaken.NotTaken;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
public class RegisterUserRequest implements Serializable {
    @NotBlank(message = "email is required")
    @Email(message = "must be a valid email")
    @Lowercase(message = "must be in lowercase")
    @NotTaken(message = "Email is allerady taken")
    private String email;
    @NotBlank(message = "first name is required")
    private String firstName;
    @NotBlank(message = "last name is required")
    private String lastName;
    @Size(min = 6, max = 25, message = "Password must be between 6 to 25 characters long.")
    private String password;

    public boolean registerExternal() {
        return password == null;
    }
}
