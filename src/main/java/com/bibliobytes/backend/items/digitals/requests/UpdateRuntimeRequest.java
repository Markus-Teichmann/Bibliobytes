package com.bibliobytes.backend.items.digitals.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateRuntimeRequest {
    @NotBlank
    private String runtime;
}
