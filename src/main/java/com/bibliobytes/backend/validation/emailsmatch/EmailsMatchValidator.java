package com.bibliobytes.backend.validation.emailsmatch;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailsMatchValidator implements ConstraintValidator<EmailsMatch, EmailRequest> {
    @Override
    public boolean isValid(EmailRequest request, ConstraintValidatorContext context) {
        return request.getNewEmail().matches(request.getConfirmNewEmail());
    }
}
