package com.bibliobytes.backend.validation.notTaken;

import com.bibliobytes.backend.users.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@AllArgsConstructor
public class NotTakenValidator implements ConstraintValidator<NotTaken, String> {
    private UserRepository userRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        Set<String> emails = userRepository.findAllEmails();
        return !emails.contains(email);
    }
}
