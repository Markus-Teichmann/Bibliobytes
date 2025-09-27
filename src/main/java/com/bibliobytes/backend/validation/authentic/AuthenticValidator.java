package com.bibliobytes.backend.validation.authentic;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class AuthenticValidator implements ConstraintValidator<Authentic, Authenticatable> {
    private AuthenticationManager authenticationManager;

    @Override
    public boolean isValid(Authenticatable value, ConstraintValidatorContext context) {
        // Sollte wenn etwas nicht stimmt eine BadCredentialsException werfen
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        value.getEmail(),
                        value.getPassword()
                )
        );
        // Folgich wenn das hier läuft, dann können wir auch gleich true zurückgeben.
        return true;
        // Falls das nicht klappt brauchen wir halt try Catch oder etwas anderes.
    }
}