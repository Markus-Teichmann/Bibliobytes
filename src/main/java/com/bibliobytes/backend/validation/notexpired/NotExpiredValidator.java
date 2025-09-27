package com.bibliobytes.backend.validation.notexpired;

import com.bibliobytes.backend.auth.services.jwe.Jwe;
import com.bibliobytes.backend.auth.services.jwe.JweService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class NotExpiredValidator implements ConstraintValidator<NotExpired, String> {
    private JweService jweService;

    @Override
    public boolean isValid(String token, ConstraintValidatorContext context) {
        Jwe jwe = jweService.parse(token);
        return jwe != null && !jwe.isExpired();
    }
}
