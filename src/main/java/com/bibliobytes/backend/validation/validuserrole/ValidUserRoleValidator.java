package com.bibliobytes.backend.validation.validuserrole;

import com.bibliobytes.backend.users.entities.Role;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidUserRoleValidator implements ConstraintValidator<ValidUserRole, Role> {

    @Override
    public boolean isValid(Role role, ConstraintValidatorContext context) {
        for (Role r: Role.values()) {
            if (r.equals(role)) {
                return true;
            }
        }
        return false;
    }
}
