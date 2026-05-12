package com.bibliobytes.backend.validation.validuserrolename;

import com.bibliobytes.backend.users.entities.Role;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.stream.Stream;

public class ValidUserRoleNameValidator implements ConstraintValidator<ValidUserRoleName, String> {
    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        return Stream.of(Role.values()).map(Enum::name).toList().contains(name);
    }
}
