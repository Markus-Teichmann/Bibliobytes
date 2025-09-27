package com.bibliobytes.backend.items.digitals.requests;

import com.bibliobytes.backend.validation.validsubtitleid.ValidSubtitleId;
import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

@Data
public class AddSubtitleRequest {
    @ValidSubtitleId
    private Long id;
    private String language;

    @AssertTrue
    private boolean isValid() {
        return (id != null || language != null) && !(id != null && language != null);
    }
}
