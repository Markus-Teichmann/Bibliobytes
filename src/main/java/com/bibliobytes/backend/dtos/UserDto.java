package com.bibliobytes.backend.dtos;

import com.bibliobytes.backend.entities.Role;
import com.bibliobytes.backend.validation.Lowercase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class UserDto {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
}
