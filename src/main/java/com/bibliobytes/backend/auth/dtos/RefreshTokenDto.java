package com.bibliobytes.backend.auth.dtos;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class RefreshTokenDto implements Serializable {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
}
