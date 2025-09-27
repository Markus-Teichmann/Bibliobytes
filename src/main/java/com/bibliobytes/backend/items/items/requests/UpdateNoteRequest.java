package com.bibliobytes.backend.items.items.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateNoteRequest {
    @NotBlank
    private String note;
}
