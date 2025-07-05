package com.bibliobytes.backend.users;

import com.bibliobytes.backend.auth.config.JweConfig;
import com.bibliobytes.backend.auth.services.Jwe;
import com.bibliobytes.backend.users.dtos.*;
import com.bibliobytes.backend.users.dtos.confirmable.RegisterUserRequest;
import com.bibliobytes.backend.users.dtos.confirmable.UpdateCredentialsDto;
import com.bibliobytes.backend.users.entities.Role;
import com.bibliobytes.backend.auth.services.JweService;
import com.bibliobytes.backend.users.entities.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.management.remote.JMXAuthenticator;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final JweConfig jweConfig;
    private final JweService jweService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody RegisterUserRequest request,
            HttpServletResponse response,
            UriComponentsBuilder uriBuilder
    ) throws Exception {
        var user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (!(user == null || (user.getRole() == Role.EXTERNAL && request.getPassword() != null))) {
            return ResponseEntity.badRequest().body(
                    Map.of("email", "Email is allready registered.")
            );
        }
        if (user == null) {
            user = userMapper.toEntity(request);
        }
        if (request.getPassword() != null) {
            // Create Token
            var confirmableToken = jweService.generateConfirmableToken(request);

            // Place Token in Response
            var cookie = new Cookie("confirmable_token", confirmableToken.toString());
            cookie.setHttpOnly(true);
            cookie.setPath("/users");
            cookie.setMaxAge((int) jweConfig.getConfirmableTokenExpiration());
            cookie.setSecure(true);
            response.addCookie(cookie);

            return ResponseEntity.ok().body(
                    Map.of(
                            "token", confirmableToken.toString(),
                            "message", "Wir haben Ihnen eine Email mit einem Bestätigungscode and ihre Emailadresse " +
                                    user.getEmail() + " geschickt."
                    )
            );
        }
        user.setRole(Role.EXTERNAL);
        userRepository.save(user);
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(uri).body(userMapper.toDto(user));
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmData(
        @Valid @RequestBody ConfirmCodeRequest codeRequest,
        @CookieValue(value = "confirmable_token") String token,
        UriComponentsBuilder uriBuilder
    ) {
        Jwe jwe = jweService.parse(token);
        if (jwe == null || jwe.isExpired()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Token expired"));
        }
        if (!codeRequest.getCode().matches(jwe.get("code", String.class))) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid code"));
        }
        User user = null;
        if(jwe.get("dto", RegisterUserRequest.class) instanceof RegisterUserRequest request) {
            request.setPassword(passwordEncoder.encode(request.getPassword()));
            user = userService.registerUser(request);
        }
        if (jwe.get("dto", UpdateCredentialsDto.class) instanceof UpdateCredentialsDto dto) {
            if (dto.getNewPassword() != null) {
                dto.setNewPassword(passwordEncoder.encode(dto.getNewPassword()));
            }
            user = userService.updateCredentials(dto);
        }
        if (user != null) {
            var uri = uriBuilder.path("/users/{id}").buildAndExpand(user.getId()).toUri();
            return ResponseEntity.created(uri).body(userMapper.toDto(user));
        }
        return ResponseEntity.internalServerError().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable UUID id) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @GetMapping("/applicants")
    public ResponseEntity<List<UserDto>> getApplicants() {
        var applicants = userRepository.findAllByRole(Role.APPLICANT)
                .stream().map(a -> userMapper.toDto(a)).toList();
        return ResponseEntity.ok(applicants);
    }

    @PutMapping("/updateCredentials")
    public ResponseEntity<?> updateCredentials(
            @Valid @RequestBody UpdateCredentialsDto request,
            HttpServletResponse response
    ) throws Exception {
        var user = userService.findMe();
        if (user == null) {
            // Wird nie passieren
            return ResponseEntity.internalServerError().build();
        }
        if (user.getRole() != Role.ADMIN && request.getId() != null) {
            return ResponseEntity.badRequest().build();
        }
        if (user.getRole() == Role.ADMIN && request.getId() != null) {
            if (request.getNewPassword() != null) {
                request.setNewPassword(passwordEncoder.encode(request.getNewPassword()));
            }
            user = userService.updateCredentials(request);
            if (user == null) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(userMapper.toDto(user));
            }
        }

        if (request.getEmail() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Email and Password are required"
            ));
        }

        // Angegebene Daten authentifizieren
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        if(request.getNewPassword() != null && !request.getNewPassword().matches(request.getConfirmNewPassword())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Passwords do not match"));
        }

        if(request.getNewEmail() != null && !request.getNewEmail().matches(request.getConfirmNewEmail())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Emails do not match"));
        }

        // Generate Token
        var confirmableToken = jweService.generateConfirmableToken(request);

        // Place Token in Response
        var cookie = new Cookie("confirmable_token", confirmableToken.toString());
        cookie.setHttpOnly(true);
        cookie.setPath("/users");
        cookie.setMaxAge((int) jweConfig.getConfirmableTokenExpiration());
        cookie.setSecure(true);
        response.addCookie(cookie);

        return ResponseEntity.ok().body(
                Map.of(
                        "token", confirmableToken.toString(),
                        "message", "Wir haben Ihnen eine Email mit einem Bestätigungscode and ihre Emailadresse " +
                                user.getEmail() + " geschickt."
                )
        );
    }

    @PutMapping("/updateProfile")
    public ResponseEntity<UserDto> updateProfile(
            @Valid @RequestBody UpdateProfileDto request
    ) {
        var user = userService.findMe();
        if (user == null) {
            // Wird nie passieren
            return ResponseEntity.internalServerError().build();
        }
        if (user.getRole() != Role.ADMIN && request.getId() != null) {
            return ResponseEntity.badRequest().build();
        }
        user = userService.updateProfile(request);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PutMapping("/updateRole")
    public ResponseEntity<?> updateRole(
        @Valid @RequestBody UpdateRole request
    ) {
        var user = userRepository.findById(request.getId()).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        if (user.getPassword() == null && request.getRole() != Role.EXTERNAL) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Nutzer ohne Passwort können die Rolle "
                            + request.getRole().name()
                            + " nicht einnehmen.")
            );
        }
        user.setRole(request.getRole());
        userRepository.save(user);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @DeleteMapping()
    public ResponseEntity<UserDto> deleteUser(
            @RequestBody(required = false) DelteUserRequest request
    ) {
        var user = userService.findMe();
        if (request != null && user.getRole() == Role.ADMIN && request.getId() != null) {
            user = userRepository.findById(request.getId()).orElse(null);
        }
        if (user == null || (request != null && user.getRole() != Role.ADMIN && request.getId() != null)) {
            return ResponseEntity.badRequest().build();
        }
        user.setRole(Role.EXTERNAL);
        userRepository.save(user);
        return ResponseEntity.ok().body(userMapper.toDto(user));
    }

    @GetMapping()
    public ResponseEntity<Map<String, List<UserDto>>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
