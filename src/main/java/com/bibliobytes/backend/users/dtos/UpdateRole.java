package com.bibliobytes.backend.users.dtos;

import com.bibliobytes.backend.users.entities.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateRole {
    @NotBlank
    private Role role;
}
