package com.bibliobytes.backend.users.dtos;

import com.bibliobytes.backend.users.entities.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateRole {
    private UUID id;
    private Role role;
}
