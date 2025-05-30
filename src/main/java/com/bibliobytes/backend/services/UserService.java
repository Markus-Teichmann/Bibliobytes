package com.bibliobytes.backend.services;

import com.bibliobytes.backend.dtos.*;
import com.bibliobytes.backend.entities.Role;
import com.bibliobytes.backend.entities.User;
import com.bibliobytes.backend.mappers.UserMapper;
import com.bibliobytes.backend.repositorys.UserRepository;
import lombok.AllArgsConstructor;
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