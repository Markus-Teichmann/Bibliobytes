package com.bibliobytes.backend.controllers;

import com.bibliobytes.backend.config.JwtConfig;
import com.bibliobytes.backend.dtos.*;
import com.bibliobytes.backend.entities.Role;
import com.bibliobytes.backend.mappers.UserMapper;
import com.bibliobytes.backend.repositorys.UserRepository;
import com.bibliobytes.backend.services.JwtService;
import com.bibliobytes.backend.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final JwtConfig jwtConfig;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping()
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody RegisterUserRequest request,
            HttpServletResponse response,
            UriComponentsBuilder uriBuilder
    ) {
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
            // Mail rausschicken
            IntStream stream = new Random().ints(6L, 0, 10);
            String code = Arrays.toString(stream.toArray());
            code = code.replaceAll("[^0-9]", "");
            System.out.println("Der Code für die Email lautet: " + code);

            // Passenden Token erstellen
            var registerToken = jwtService.generateRegisterRequestToken(request, code);

            // Token in die Antwort packen!
            var cookie = new Cookie("register_request_token", registerToken.toString());
            cookie.setHttpOnly(true);
            cookie.setPath("/users");
            cookie.setMaxAge((int) jwtConfig.getRegisterRequestTokenExpiration());
            cookie.setSecure(true);
            response.addCookie(cookie);

            // Antwort raus schicken.
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "token", registerToken.toString(),
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

    @PostMapping("/code")
    public ResponseEntity<?> confirmCode(
        @Valid @RequestBody ConfirmCodeRequest codeRequest,
        @CookieValue(value = "register_request_token") String token,
        UriComponentsBuilder uriBuilder
    ) {
        var jwt = jwtService.parse(token);
        if (jwt == null || jwt.isExpired()) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Token expired")
            );
        }
        if (!codeRequest.getCode().matches(jwt.get("code", String.class))) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Invalid code")
            );
        }
        var user = userRepository.findByEmail(jwt.getSubject()).orElse(null);
        if (user == null) {
            user = userMapper.toEntity(jwt);
        } else {
            user.setPassword(jwt.get("password", String.class));
        }
        userRepository.save(user);
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(uri).body(userMapper.toDto(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable UUID id) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @GetMapping()
    public Map<String, List<UserDto>> getAllUsers() {
        return userService.getAllUsers();
    }
}
