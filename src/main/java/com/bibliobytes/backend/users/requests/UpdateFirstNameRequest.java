package com.bibliobytes.backend.users.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateFirstNameRequest {
    @NotBlank(message = "Firstname required")
    private String firstName;
}
