package com.bibliobytes.backend.donations.requests;

import com.bibliobytes.backend.validation.validuserid.ValidUserId;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateOwnerRequest {
    @ValidUserId
    private UUID ownerId;
}
