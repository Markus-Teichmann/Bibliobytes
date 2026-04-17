package com.bibliobytes.backend.validation.validpassword;

import com.bibliobytes.backend.users.UserRepository;
import com.bibliobytes.backend.users.UserService;
import com.bibliobytes.backend.users.entities.User;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class ValidPasswordValidator implements ConstraintValidator<ValidPassword, String> {
    private UserService userService;
    private AuthenticationManager authenticationManager;

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        String email = userService.findMe().getEmail();
        // Genau so gelöst, wie in AuthenticValidator
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        return true;
    }
}
