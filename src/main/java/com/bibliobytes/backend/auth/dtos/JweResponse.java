package com.bibliobytes.backend.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JweResponse {
    private String token;
}
