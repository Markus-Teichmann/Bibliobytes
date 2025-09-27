package com.bibliobytes.backend.validation.unlocked;

import com.bibliobytes.backend.users.UserRepository;
import com.bibliobytes.backend.users.entities.Role;
import com.bibliobytes.backend.users.entities.User;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UnlockedValidator implements ConstraintValidator<Unlocked, String> {
    private UserRepository userRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        User user = userRepository.findByEmail(email).orElse(null);
        return user != null && user.getRole() != Role.APPLICANT;
    }
}
