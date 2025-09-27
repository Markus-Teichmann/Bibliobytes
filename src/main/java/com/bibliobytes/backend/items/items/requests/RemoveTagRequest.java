package com.bibliobytes.backend.items.items.requests;

import com.bibliobytes.backend.validation.validtagid.ValidTagId;
import lombok.Data;

@Data
public class RemoveTagRequest {
    @ValidTagId
    private Long id;
}
