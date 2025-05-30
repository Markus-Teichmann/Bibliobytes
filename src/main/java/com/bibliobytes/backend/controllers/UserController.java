package com.bibliobytes.backend.controllers;

import com.bibliobytes.backend.config.JwtConfig;
import com.bibliobytes.backend.dtos.*;
import com.bibliobytes.backend.mappers.UserMapper;
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
@AllArgsConstructor
public class UserController {
    private final JwtConfig jwtConfig;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/registration")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody RegisterUserRequest request,
            HttpServletResponse response
    ) {
        if (userService.existsByEmail(request)) {
            return ResponseEntity.badRequest().body(
                    Map.of("email", "Email is allready registered.")
            );
        }

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
                                request.getEmail() + " geschickt."
                )
        );
    }

    @PostMapping("/externals")
    public ResponseEntity<?> registerProfile(
        @Valid @RequestBody RegisterProfileRequest profileRequest,
        UriComponentsBuilder uriBuilder
    ) {
        var request = userMapper.toUserRequest(profileRequest);
        UserDto userDto = userService.createUser(request);
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri();
        return ResponseEntity.created(uri).body(userDto);
    }

    @PostMapping("/users")
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
        var request = userMapper.toUserRequest(jwt);
        UserDto userDto = userService.createUser(request);
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri();
        return ResponseEntity.created(uri).body(userDto);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable UUID id) {
        var user = userService.getUserById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users")
    public Map<String, List<UserDto>> getAllUsers() {
        return userService.getAllUsers();
    }
}
