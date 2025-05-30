package com.bibliobytes.backend.dtos;

import com.bibliobytes.backend.entities.Role;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString
public class UserDto {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
}
