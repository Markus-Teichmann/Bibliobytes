package com.bibliobytes.backend.users;

import com.bibliobytes.backend.users.dtos.UserDto;
import com.bibliobytes.backend.users.entities.Role;
import com.bibliobytes.backend.users.entities.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.*;

@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User findMe() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = UUID.fromString((String) authentication.getPrincipal());
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    public Map<String, List<UserDto>> getAllUsers() {
        return Map.of(
            "externals", userRepository.findAllByRole(Role.EXTERNAL).stream().map(userMapper::toDto).toList(),
            "applicants", userRepository.findAllByRole(Role.APPLICANT).stream().map(userMapper::toDto).toList(),
            "users", userRepository.findAllByRole(Role.USER).stream().map(userMapper::toDto).toList(),
            "services", userRepository.findAllByRole(Role.SERVICE).stream().map(userMapper::toDto).toList(),
            "admins", userRepository.findAllByRole(Role.ADMIN).stream().map(userMapper::toDto).toList()
        );
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
        );
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.emptyList()
        );
    }
}