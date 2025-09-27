package com.bibliobytes.backend.users.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateLastNameRequest {
    @NotBlank(message = "Last Name required")
    private String lastName;
}
