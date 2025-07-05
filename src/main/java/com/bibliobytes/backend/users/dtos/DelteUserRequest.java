package com.bibliobytes.backend.users.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class DelteUserRequest {
    private UUID id;
}
