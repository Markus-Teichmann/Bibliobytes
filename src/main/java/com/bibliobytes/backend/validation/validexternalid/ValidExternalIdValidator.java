package com.bibliobytes.backend.validation.validexternalid;

import com.bibliobytes.backend.users.UserRepository;
import com.bibliobytes.backend.users.entities.Role;
import com.bibliobytes.backend.users.entities.User;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class ValidExternalIdValidator implements ConstraintValidator<ValidExternalId, UUID> {
    private UserRepository userRepository;

    @Override
    public boolean isValid(UUID id, ConstraintValidatorContext context) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return false;
        }
        return user.getRole() == Role.EXTERNAL;
    }
}
