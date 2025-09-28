package com.bibliobytes.backend.users;

import com.bibliobytes.backend.auth.TokenMapper;
import com.bibliobytes.backend.auth.config.JweConfig;
import com.bibliobytes.backend.auth.dtos.JweResponse;
import com.bibliobytes.backend.auth.services.jwe.Jwe;
import com.bibliobytes.backend.auth.services.jwe.JweService;
import com.bibliobytes.backend.items.ItemService;
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
    private final JweConfig jweConfig;
    private final TokenMapper tokenMapper;
    private final RentalRepository rentalRepository;

    public UserDto registerExternal(RegisterUserRequest registerUserRequest) {
        User user = userMapper.toEntity(registerUserRequest);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    public User registerExternal(String email, String firstName, String lastName) {
        User user = userMapper.toExternal(email, firstName, lastName);
        userRepository.save(user);
        return user;
    }

    public Cookie generateRegisterCookie(RegisterUserRequest request, JweService jweService) throws Exception {
        Jwe token = jweService.generateRegisterUserToken(request);
        Cookie cookie = new Cookie("register_token", token.toString());
        cookie.setHttpOnly(true);
        cookie.setPath("/users");
        cookie.setMaxAge((int) jweConfig.getRegisterUserTokenExpiration());
        cookie.setSecure(true);
        return cookie;
    }

    public Cookie generateRefreshCookie(String email, JweService jweService) throws Exception {
        User user = userRepository.findByEmail(email).orElse(null);
        Jwe refreshToken = jweService.generateRefreshToken(tokenMapper.toRefreshTokenDto(user));
        Cookie cookie = new Cookie("refresh_token", refreshToken.toString());
        cookie.setHttpOnly(true);
        cookie.setPath("/users");
        cookie.setMaxAge((int) jweConfig.getRefreshTokenExpiration());
        cookie.setSecure(true);
        return cookie;
    }

    public JweResponse generateJweResponse(User user, JweService jweService) throws Exception {
        Jwe accessToken = jweService.generateAccessToken(tokenMapper.toAccessTokenDto(user));
        return new JweResponse(accessToken.toString());
    }

    /*
        In dieser Methode kann der String sowohl ein Token als auch die Email eines Users sein.
     */
    public JweResponse generateJweResponse(String string, JweService jweService) throws Exception {
        User user = userRepository.findByEmail(string).orElse(null);
        if (user == null) {
            Jwe jwe = jweService.parse(string);
            user = userRepository.findById(UUID.fromString(jwe.getSubject())).orElseThrow();
        }
        return generateJweResponse(user, jweService);
    }

    public UserDto registerUser(RegisterUserRequest request) {
        var user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            user = userMapper.toEntity(request);
        }
        user.setPassword(request.getPassword());
        userRepository.save(user);
        return userMapper.toDto(user);
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

    public UserDto updateFirstName(UUID id, UpdateFirstNameRequest request) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setFirstName(request.getFirstName());
            userRepository.save(user);
        }
        return userMapper.toDto(user);
    }

    public UserDto updateFirstName(UpdateFirstNameRequest request) {
        return updateFirstName(getMyId(), request);
    }

    public UserDto updateLastName(UUID id, UpdateLastNameRequest request) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setFirstName(request.getLastName());
            userRepository.save(user);
        }
        return userMapper.toDto(user);
    }

    public UserDto updateLastName(UpdateLastNameRequest request) {
        return updateLastName(getMyId(), request);
    }

    public UserDto updateEmail(UUID id, UpdateEmailRequest request) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null && request.getNewEmail() != null) {
            user.setEmail(request.getNewEmail());
            userRepository.save(user);
        }
        return userMapper.toDto(user);
    }

    public Cookie generateUpdateEmailCookie(UpdateEmailRequest request, JweService jweService) throws Exception {
        UUID id = getMyId();
        Jwe token = jweService.generateUpdateEmailToken(id, request);
        var cookie = new Cookie("update_email_token", token.toString());
        cookie.setHttpOnly(true);
        cookie.setPath("/me");
        cookie.setMaxAge((int) jweConfig.getUpdateUserCredentialsTokenExpiration());
        cookie.setSecure(true);
        return cookie;
    }

    public UserDto updateEmail(Jwe token) {
        UpdateEmailRequest request = token.toDto();
        UUID id = UUID.fromString(token.getSubject());
        return updateEmail(id, request);
    }

    public UserDto updatePassword(UUID id, UpdatePasswordRequest request) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null && request.getNewPassword() != null) {
            user.setPassword(request.getNewPassword());
            userRepository.save(user);
        }
        return userMapper.toDto(user);
    }

    public Cookie generateUpdatePasswordCookie(UpdatePasswordRequest request, JweService jweService) throws Exception {
        UserDto me = findMe();
        Jwe token = jweService.generateUpdatePasswordToken(me.getId(), request, me.getEmail());
        var cookie = new Cookie("update_password_token", token.toString());
        cookie.setHttpOnly(true);
        cookie.setPath("/me");
        cookie.setMaxAge((int) jweConfig.getUpdateUserCredentialsTokenExpiration());
        cookie.setSecure(true);
        return cookie;
    }

    public UserDto updatePassword(Jwe token) {
        UpdatePasswordRequest request = token.toDto();
        UUID id = UUID.fromString(token.getSubject());
        return updatePassword(id, request);
    }

    public Set<UserDto> getApplicants() {
        return userRepository.findAllByRole(Role.APPLICANT).stream()
                .map(a -> userMapper.toDto(a)).collect(Collectors.toSet());
    }

    public UserDto updateRole(UUID id, UpdateRoleRequest request) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setRole(request.getRole());
            userRepository.save(user);
        }
        return userMapper.toDto(user);
    }

    public UserDto deleteUser() {
        UUID id = getMyId();
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setRole(Role.EXTERNAL);
            userRepository.save(user);
        }
        return userMapper.toDto(user);
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

    public UserDto findMe() {
        User user = userRepository.findById(getMyId()).orElseThrow(UserNotFoundException::new);
        return userMapper.toDto(user);
    }

    public Set<UserDto> getAllUsers() {
        Set<UserDto> set = userRepository.findAllByRole(Role.EXTERNAL).stream().map(userMapper::toDto).collect(Collectors.toSet());
        set.addAll(userRepository.findAllByRole(Role.APPLICANT).stream().map(userMapper::toDto).collect(Collectors.toSet()));
        set.addAll(userRepository.findAllByRole(Role.USER).stream().map(userMapper::toDto).collect(Collectors.toSet()));
        set.addAll(userRepository.findAllByRole(Role.SERVICE).stream().map(userMapper::toDto).collect(Collectors.toSet()));
        set.addAll(userRepository.findAllByRole(Role.ADMIN).stream().map(userMapper::toDto).collect(Collectors.toSet()));
        return set;
    }

    public UserDto getUser(UUID id) {
        User user = userRepository.findById(id).orElse(null);
        return userMapper.toDto(user);
    }

    public Set<RentalDto> getRentals(UUID userId, RentalService rentalService, ItemService itemService) {
        User user = userRepository.findById(userId).orElse(null);
        return rentalRepository.findAllRentalsByUser(user).stream()
                .map(rental -> rentalService.toDto(rental, itemService))
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