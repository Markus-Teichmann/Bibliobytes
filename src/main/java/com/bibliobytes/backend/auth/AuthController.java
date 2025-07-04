package com.bibliobytes.backend.auth;

import com.bibliobytes.backend.auth.config.JweConfig;
import com.bibliobytes.backend.auth.dtos.LoginRequest;
import com.bibliobytes.backend.users.UserMapper;
import com.bibliobytes.backend.auth.dtos.JweResponse;
import com.bibliobytes.backend.auth.services.JweService;
import com.bibliobytes.backend.users.UserRepository;
import com.bibliobytes.backend.users.UserService;
import com.bibliobytes.backend.users.dtos.UserDto;
import com.bibliobytes.backend.users.entities.Role;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.TokenService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JweService jweService;
    private final JweConfig config;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final TokenMapper tokenMapper;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) throws Exception {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null || user.getRole() == Role.EXTERNAL) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("message", "Sie müssen sich zuerst registrieren.")
            );
        }
        if (user.getRole() == Role.APPLICANT) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("message", "Sie müssen noch von einem Admin freigeschaltet werden.")
            );
        }

        var accessToken = jweService.generateAccessToken(tokenMapper.toAccessTokenDto(user));
        var refreshToken = jweService.generateRefreshToken(tokenMapper.toRefreshTokenDto(user));

        var cookie = new Cookie("refresh_token", refreshToken.toString());
        cookie.setHttpOnly(true);
        cookie.setPath("/auth");
        cookie.setMaxAge((int) config.getRefreshTokenExpiration());//7 Tage
        cookie.setSecure(true);
        response.addCookie(cookie);

        return ResponseEntity.ok(new JweResponse(accessToken.toString()));
    }

    @GetMapping("/refresh")
    public ResponseEntity<JweResponse> refreshToken(
            @CookieValue(value = "refresh_token") String refreshToken // value = "name_des_cookies" vgl. login
    ) throws Exception {
        var jwe = jweService.parse(refreshToken);
        if (jwe == null || jwe.isExpired()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var user = userRepository.findById(UUID.fromString(jwe.getSubject())).orElseThrow();
        var accessToken = jweService.generateAccessToken(tokenMapper.toAccessTokenDto(user));

        return ResponseEntity.ok(new JweResponse(accessToken.toString()));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me() {
        var user = userService.findMe();
        var userDto = userMapper.toDto(user);
        return ResponseEntity.ok(userDto);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleUserNotFound() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
