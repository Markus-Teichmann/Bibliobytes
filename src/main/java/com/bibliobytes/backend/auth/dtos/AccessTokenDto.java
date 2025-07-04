package com.bibliobytes.backend.auth.dtos;

import com.bibliobytes.backend.users.entities.Role;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class AccessTokenDto implements Serializable {
    private UUID id;
    private Role role;
}
