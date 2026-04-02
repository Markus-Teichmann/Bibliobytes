package com.bibliobytes.backend.users.requests;

import com.bibliobytes.backend.validation.notTaken.NotTaken;
import lombok.Data;

@Data
public class UniqueEmailRequest {
    @NotTaken(message = "Email is already taken")
    private String email;
}
