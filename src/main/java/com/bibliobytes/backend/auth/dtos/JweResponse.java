package com.bibliobytes.backend.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class JweResponse {
    private String token;
    private Date expires;
}
