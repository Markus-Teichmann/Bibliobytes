package com.bibliobytes.backend.items.digitals.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateLabelRequest {
    @NotBlank
    private String label;
}
