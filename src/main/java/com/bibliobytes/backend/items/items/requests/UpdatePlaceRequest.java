package com.bibliobytes.backend.items.items.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePlaceRequest {
    @NotBlank
    private String place;
}
