package com.bibliobytes.backend.items.digitals.requests;

import com.bibliobytes.backend.validation.validlanguageid.ValidLanguageId;
import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

@Data
public class RemoveLanguageRequest {
    @ValidLanguageId
    private Long id;
    private String name;

    @AssertTrue
    private boolean isValid() {
        return (id != null || name != null) && !(id != null && name != null);
    }
}
