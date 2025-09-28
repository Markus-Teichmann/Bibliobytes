package com.bibliobytes.backend.auth.dtos;

import com.bibliobytes.backend.users.entities.Role;
import com.bibliobytes.backend.validation.validuserid.ValidUserId;
import com.bibliobytes.backend.validation.validuserrole.ValidUserRole;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class AccessTokenDto implements Serializable {
    @ValidUserId
    private UUID id;
    @ValidUserRole
    private Role role;
}
