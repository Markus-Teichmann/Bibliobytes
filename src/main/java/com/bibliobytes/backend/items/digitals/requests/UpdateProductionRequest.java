package com.bibliobytes.backend.items.digitals.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProductionRequest {
    @NotBlank
    private String production;
}
