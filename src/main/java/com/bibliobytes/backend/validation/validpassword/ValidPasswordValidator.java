package com.bibliobytes.backend.validation.validpassword;

import com.bibliobytes.backend.users.UserRepository;
import com.bibliobytes.backend.users.UserService;
import com.bibliobytes.backend.users.entities.User;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class ValidPasswordValidator implements ConstraintValidator<ValidPassword, String> {
    private UserService userService;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        UUID myId = userService.getMyId();
        User me = userRepository.findById(myId).orElse(null);
        if (me == null) {
            return false;
        }
        return passwordEncoder.matches(password, me.getPassword());
    }
}
