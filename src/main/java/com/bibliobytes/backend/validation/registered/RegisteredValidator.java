package com.bibliobytes.backend.validation.registered;

import com.bibliobytes.backend.users.UserRepository;
import com.bibliobytes.backend.users.entities.Role;
import com.bibliobytes.backend.users.entities.User;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@AllArgsConstructor
public class RegisteredValidator implements ConstraintValidator<Registered, String> {
    private UserRepository userRepository;


    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        User user = userRepository.findByEmail(email).orElse(null);
        return user != null && user.getRole() != Role.EXTERNAL;
    }
}
