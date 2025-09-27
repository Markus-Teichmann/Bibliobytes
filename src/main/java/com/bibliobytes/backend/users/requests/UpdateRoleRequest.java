package com.bibliobytes.backend.users.requests;

import com.bibliobytes.backend.users.entities.Role;
import com.bibliobytes.backend.validation.validuserrole.ValidUserRole;
import lombok.Data;

@Data
public class UpdateRoleRequest {
    @ValidUserRole
    private Role role;
}
