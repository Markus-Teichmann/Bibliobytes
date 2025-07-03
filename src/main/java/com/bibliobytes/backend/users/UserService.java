package com.bibliobytes.backend.users;

import com.bibliobytes.backend.users.dtos.RegisterUserRequest;
import com.bibliobytes.backend.users.dtos.UpdateCredentialsDto;
import com.bibliobytes.backend.users.dtos.UpdateProfileDto;
import com.bibliobytes.backend.users.dtos.UserDto;
import com.bibliobytes.backend.users.entities.Role;
import com.bibliobytes.backend.users.entities.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.*;

@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    public User registerUser(RegisterUserRequest request) {
        var user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            user = userMapper.toEntity(request);
        }
        user.setPassword(request.getPassword());
        userRepository.save(user);
        return user;
    }

    public User updateCredentials(UpdateCredentialsDto dto) {
        var user = userRepository.findById(dto.getId()).orElse(null);
        if (user != null) {
            user.setEmail(dto.getEmail());
            user.setPassword(dto.getPassword());
            userRepository.save(user);
        }
        return user;
    }

    public User updateProfile(UpdateProfileDto dto) {
        var user = userRepository.findById(dto.getId()).orElse(null);
        if (user != null) {
            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
            userRepository.save(user);
        }
        return user;
    }

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