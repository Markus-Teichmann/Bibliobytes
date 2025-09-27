package com.bibliobytes.backend.users.requests;

import com.bibliobytes.backend.validation.authentic.Authentic;
import com.bibliobytes.backend.validation.authentic.Authenticatable;
import com.bibliobytes.backend.validation.lowercase.Lowercase;
import com.bibliobytes.backend.validation.registered.Registered;
import com.bibliobytes.backend.validation.unlocked.Unlocked;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Authentic
@Data
public class LoginRequest implements Authenticatable {
    @NotBlank
    @Email(message = "must be a valid email")
    @Lowercase(message = "must be in lowercase")
    @Registered(message = "User must be registered")
    @Unlocked(message = "User must be unlocked by an Admin.")
    String email;
    @NotBlank
    @Size(min = 6, max = 25, message = "Invalid Password.")
    String password;
}
