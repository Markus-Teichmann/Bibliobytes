package com.bibliobytes.backend.users.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateProfileDto {
    private UUID id; // Nur zur Identifikation wird natürlich nicht verändert.
    private String firstName;
    private String lastName;
}
