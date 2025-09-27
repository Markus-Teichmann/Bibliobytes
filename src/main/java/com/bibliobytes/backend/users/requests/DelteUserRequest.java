package com.bibliobytes.backend.users.requests;

import com.bibliobytes.backend.validation.validuserid.ValidUserId;
import lombok.Data;

import java.util.UUID;

@Data
public class DelteUserRequest {
    @ValidUserId
    private UUID id;
}
