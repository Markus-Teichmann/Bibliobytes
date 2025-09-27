package com.bibliobytes.backend.items.items.requests;

import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

@Data
public class AddTagRequest {
    private Long id;
    private String name;

    @AssertTrue
    private boolean isValid() {
        return (id != null || name != null) && !(id != null && name != null);
    }
}
