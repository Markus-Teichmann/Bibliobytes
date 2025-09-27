package com.bibliobytes.backend.users;

import com.bibliobytes.backend.auth.TokenMapper;
import com.bibliobytes.backend.auth.config.JweConfig;
import com.bibliobytes.backend.auth.dtos.JweResponse;
import com.bibliobytes.backend.auth.services.jwe.Jwe;
import com.bibliobytes.backend.auth.services.jwe.JweService;
import com.bibliobytes.backend.rentals.RentalRepository;
import com.bibliobytes.backend.rentals.RentalService;
import com.bibliobytes.backend.rentals.dtos.RentalDto;
import com.bibliobytes.backend.users.dtos.*;
import com.bibliobytes.backend.users.entities.Role;
import com.bibliobytes.backend.users.entities.User;
import com.bibliobytes.backend.users.requests.*;
import jakarta.servlet.http.Cookie;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JweService jweService;
    private final JweConfig jweConfig;
    private final TokenMapper tokenMapper;
    private final RentalRepository rentalRepository;
    private final RentalService rentalService;

    public User registerExternal(RegisterUserRequest registerUserRequest) {
        User user = userMapper.toEntity(registerUserRequest);
        userRepository.save(user);
        return user;
    }

    public User registerExternal(String email, String firstName, String lastName) {
        User user = userMapper.toExternal(email, firstName, lastName);
        userRepository.save(user);
        return user;
    }

    public Cookie generateRegisterCookie(RegisterUserRequest request) throws Exception {
        Jwe token = jweService.generateRegisterUserToken(request);
        Cookie cookie = new Cookie("register_token", token.toString());
        cookie.setHttpOnly(true);
        cookie.setPath("/users");
        cookie.setMaxAge((int) jweConfig.getRegisterUserTokenExpiration());
        cookie.setSecure(true);
        return cookie;
    }

    public Cookie generateRefreshCookie(User user) throws Exception {
        Jwe refreshToken = jweService.generateRefreshToken(tokenMapper.toRefreshTokenDto(user));
        Cookie cookie = new Cookie("refresh_token", refreshToken.toString());
        cookie.setHttpOnly(true);
        cookie.setPath("/users");
        cookie.setMaxAge((int) jweConfig.getRefreshTokenExpiration());
        cookie.setSecure(true);
        return cookie;
    }

    public JweResponse generateJweResponse(User user) throws Exception {
        Jwe accessToken = jweService.generateAccessToken(tokenMapper.toAccessTokenDto(user));
        return new JweResponse(accessToken.toString());
    }

    public JweResponse generateJweResponse(String token) throws Exception {
        Jwe jwe = jweService.parse(token);
        User user = userRepository.findById(UUID.fromString(jwe.getSubject())).orElseThrow();
        return generateJweResponse(user);
    }

    public User registerUser(RegisterUserRequest request) {
        var user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            user = userMapper.toEntity(request);
        }
        user.setPassword(request.getPassword());
        userRepository.save(user);
        return user;
    }

//    public User updateCredentials(UUID id, UpdateCredentialsRequest request) {
//        User user = userRepository.findById(id).orElse(null);
//
//        if (dto.getId() == null) {
//            user = userRepository.findByEmail(dto.getOldEmail()).orElseThrow();
//        } else {
//            user = userRepository.findById(dto.getId()).orElseThrow();
//        }
//        if (dto.getNewEmail() != null) {
//            user.setEmail(dto.getNewEmail());
//        }
//        if (dto.getNewPassword() != null) {
//            user.setPassword(dto.getNewPassword());
//        }
//        userRepository.save(user);
//
//        return user;
//    }

    public User updateFirstName(UUID id, UpdateFirstNameRequest request) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setFirstName(request.getFirstName());
            userRepository.save(user);
        }
        return user;
    }

    public User updateFirstName(UpdateFirstNameRequest request) {
        return updateFirstName(getMyId(), request);
    }

    public User updateLastName(UUID id, UpdateLastNameRequest request) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setFirstName(request.getLastName());
            userRepository.save(user);
        }
        return user;
    }

    public User updateLastName(UpdateLastNameRequest request) {
        return updateLastName(getMyId(), request);
    }

    public User updateEmail(UUID id, UpdateEmailRequest request) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null && request.getNewEmail() != null) {
            user.setEmail(request.getNewEmail());
            userRepository.save(user);
        }
        return user;
    }

    public Cookie generateUpdateEmailCookie(UpdateEmailRequest request) throws Exception {
        UUID id = getMyId();
        Jwe token = jweService.generateUpdateEmailToken(id, request);
        var cookie = new Cookie("update_email_token", token.toString());
        cookie.setHttpOnly(true);
        cookie.setPath("/me");
        cookie.setMaxAge((int) jweConfig.getUpdateUserCredentialsTokenExpiration());
        cookie.setSecure(true);
        return cookie;
    }

    public User updateEmail(Jwe token) {
        UpdateEmailRequest request = token.toDto();
        UUID id = UUID.fromString(token.getSubject());
        return updateEmail(id, request);
    }

    public User updatePassword(UUID id, UpdatePasswordRequest request) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null && request.getNewPassword() != null) {
            user.setPassword(request.getNewPassword());
            userRepository.save(user);
        }
        return user;
    }

    public Cookie generateUpdatePasswordCookie(UpdatePasswordRequest request) throws Exception {
        User me = findMe();
        Jwe token = jweService.generateUpdatePasswordToken(me.getId(), request, me.getEmail());
        var cookie = new Cookie("update_password_token", token.toString());
        cookie.setHttpOnly(true);
        cookie.setPath("/me");
        cookie.setMaxAge((int) jweConfig.getUpdateUserCredentialsTokenExpiration());
        cookie.setSecure(true);
        return cookie;
    }

    public User updatePassword(Jwe token) {
        UpdatePasswordRequest request = token.toDto();
        UUID id = UUID.fromString(token.getSubject());
        return updatePassword(id, request);
    }

    public User updateRole(UUID id, UpdateRoleRequest request) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setRole(request.getRole());
            userRepository.save(user);
        }
        return user;
    }

    public User deleteUser() {
        UUID id = getMyId();
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setRole(Role.EXTERNAL);
            userRepository.save(user);
        }
        return user;
    }

//    public User updateProfile(UUID id, UpdateProfileRequest dto) {
//        User user = userRepository.findById(id).orElse(null);
//        if (user != null) {
//            if (dto.getFirstName() != null) {
//                user.setFirstName(dto.getFirstName());
//            }
//            if (dto.getLastName() != null) {
//                user.setLastName(dto.getLastName());
//            }
//            userRepository.save(user);
//        }
//        return user;
//    }

    public UUID getMyId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString((String) authentication.getPrincipal());
    }

    public User findMe() {
        return userRepository.findById(getMyId()).orElseThrow(UserNotFoundException::new);
    }

    public Set<UserDto> getAllUsers() {
        Set<UserDto> set = userRepository.findAllByRole(Role.EXTERNAL).stream().map(userMapper::toDto).collect(Collectors.toSet());
        set.addAll(userRepository.findAllByRole(Role.APPLICANT).stream().map(userMapper::toDto).collect(Collectors.toSet()));
        set.addAll(userRepository.findAllByRole(Role.USER).stream().map(userMapper::toDto).collect(Collectors.toSet()));
        set.addAll(userRepository.findAllByRole(Role.SERVICE).stream().map(userMapper::toDto).collect(Collectors.toSet()));
        set.addAll(userRepository.findAllByRole(Role.ADMIN).stream().map(userMapper::toDto).collect(Collectors.toSet()));
        return set;
    }

    public Set<RentalDto> getRentals(UUID userId) {
        User user = userRepository.findById(userId).orElse(null);
        return rentalRepository.findAllRentalsByUser(user).stream()
                .map(rental -> rentalService.toDto(rental))
                .collect(Collectors.toSet());
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