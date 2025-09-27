package com.bibliobytes.backend.donations.requests;

import com.bibliobytes.backend.validation.validitemid.ValidItemId;
import lombok.Data;

@Data
public class UpdateItemRequest {
    @ValidItemId
    private Long itemId;
}
