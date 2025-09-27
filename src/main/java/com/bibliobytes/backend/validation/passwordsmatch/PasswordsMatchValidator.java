package com.bibliobytes.backend.validation.passwordsmatch;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, PasswordRequest> {
    @Override
    public boolean isValid(PasswordRequest request, ConstraintValidatorContext context) {
        return request.getNewPassword().matches(request.getConfirmNewPassword());
    }
}
