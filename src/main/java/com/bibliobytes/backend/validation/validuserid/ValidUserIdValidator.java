package com.bibliobytes.backend.validation.validuserid;

import com.bibliobytes.backend.users.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@AllArgsConstructor
public class ValidUserIdValidator implements ConstraintValidator<ValidUserId, UUID> {
    private UserRepository userRepository;

    @Override
    public boolean isValid(UUID value, ConstraintValidatorContext context) {
        Set<UUID> ids = userRepository.findAllIds();
        return ids.contains(value);
    }
}
