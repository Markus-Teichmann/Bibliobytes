package com.bibliobytes.backend.services;

import com.bibliobytes.backend.dtos.*;
import com.bibliobytes.backend.entities.External;
import com.bibliobytes.backend.entities.Internal;
import com.bibliobytes.backend.entities.Role;
import com.bibliobytes.backend.mappers.UserMapper;
import com.bibliobytes.backend.repositorys.ExternalRepository;
import com.bibliobytes.backend.repositorys.InternalRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final ExternalRepository externalRepository;
    private final InternalRepository internalRepository;
    private final UserMapper userMapper;

    public UserDto createUser(RegisterUserRequest request) {
        if (request.getPassword() == null) {
            var external = userMapper.toExternal(request);
            externalRepository.save(external);
            return userMapper.toUserDto(external);
        } else {
            var external = externalRepository.findByEmail(request.getEmail()).orElse(null);
            Internal internal = null;
            if (external == null) {
                internal = userMapper.toInternal(request, Role.APPLICANT);
                userMapper.updateExternal(request, internal.getExternal());
                externalRepository.save(internal.getExternal());

            } else {
                internal = new Internal(external);
                userMapper.updateInternal(request, internal);
                userMapper.updateInternal(Role.APPLICANT, internal);
            }
            internalRepository.save(internal);
            return userMapper.toUserDto(internal);
        }
    }

    public Optional<UserDto> getUserById(UUID id) {
        if (internalRepository.existsById(id)) {
            var internal = internalRepository.findById(id).orElse(null);
            var userDto = userMapper.toUserDto(internal);
            return Optional.of(userDto);
        }
        if (externalRepository.existsById(id)) {
            var external = externalRepository.findById(id).orElse(null);
            var userDto = userMapper.toUserDto(external);
            return Optional.of(userDto);
        }
        return Optional.empty();
    }

    public Map<String, List<UserDto>> getAllUsers() {
        List<UserDto> externals = externalRepository.getAll().stream().map(e -> userMapper.toUserDto(e)).toList();
        List<UserDto> internals = internalRepository.getAll().stream().map(i -> userMapper.toUserDto(i)).toList();
        return Map.of(
                "externals: ", externals,
                "internals: ", internals
        );
    }

    public boolean existsByEmail(RegisterUserRequest request) {
        if (request.getPassword() == null) {
            var external = externalRepository.findByEmail(request.getEmail());
            return external.isPresent();
        } else {
            var internal = internalRepository.findByEmail(request.getEmail());
            return internal.isPresent();
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var internal = internalRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
        );
        return new User(
                internal.getExternal().getEmail(),
                internal.getPassword(),
                Collections.emptyList()
        );
    }
}
