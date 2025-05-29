package com.bibliobytes.backend.controllers;

import com.bibliobytes.backend.config.JwtConfig;
import com.bibliobytes.backend.dtos.*;
import com.bibliobytes.backend.mappers.UserMapper;
import com.bibliobytes.backend.services.Jwt;
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
@RequestMapping("/users")
public class UserController {
    private final JwtConfig jwtConfig;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping()
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody(required = false) RegisterUserRequest registerRequest,
            @Valid @RequestBody(required = false) ConfirmCodeRequest confirmRequest,
            @CookieValue(value = "register_request_token", required = false) String registerRequestToken,
            HttpServletResponse response,
            UriComponentsBuilder uriBuilder
    ) {
        if (
                registerRequest == null &&
                (confirmRequest == null || registerRequestToken == null)
        ) {
            return ResponseEntity.badRequest().build();
        }
        if (registerRequest == null) {
            Jwt registerToken = jwtService.parse(registerRequestToken);
            registerRequest = userMapper.toUserRequest(registerToken);
        }
        if (userService.existsByEmail(registerRequest)) {
            return ResponseEntity.badRequest().body(
                    Map.of("email", "Email is allready registered.")
            );
        }
        if (registerRequest.getPassword() != null) {
            Jwt registerToken = null;
            if (registerRequestToken != null) {
                registerToken = jwtService.parse(registerRequestToken);
            }

            // Passende Antworten erstellen
            Map<String, String> map = new HashMap<>();
            if (userService.existsByEmail(registerRequest)) {
                map.put("message", "Die angegebene Email wird bereits verwendet.");
                return ResponseEntity.badRequest().body(map);
            } else if (registerToken == null) {
                map.put("message: ", "Wir haben Ihnen einen sechsstelligen Code an Ihre Email-Adresse: "
                        + registerRequest.getEmail() + "geschickt.");
            } else if (registerToken.isExpired()) {
                map.put("message: ", "Ihr Code ist abgelaufen. Wir haben Ihnen einen neuen zugeschickt.");
            } else if (
                    registerToken.get("code", String.class) == null ||
                    !confirmRequest.getCode().matches(registerToken.get("code", String.class))
            ) {
                map.put("message: ", "Der eingegebene Code war falsch. Wir haben Ihnen einen neuen zugeschickt.");
            }

            // Irgendwas ist schief gelaufen.
            if (
                    registerToken == null ||
                    registerToken.isExpired() ||
                    registerToken.get("code", String.class) == null ||
                    !confirmRequest.getCode().matches(registerToken.get("code", String.class))
            ) {

                // Mail rausschicken
                IntStream stream = new Random().ints(6L, 0, 10);
                String code = Arrays.toString(stream.toArray());
                code = code.replaceAll("[^0-9]", "");
                System.out.println("Der Code für die Email lautet: " + code);

                // Passenden Token erstellen
                registerToken = jwtService.generateRegisterRequestToken(registerRequest, code);

                // Token in die Antwort packen!
                var cookie = new Cookie("register_request_token", registerToken.toString());
                cookie.setHttpOnly(true);
                cookie.setPath("/users");
                cookie.setMaxAge((int) jwtConfig.getRegisterRequestTokenExpiration());
                cookie.setSecure(true);
                response.addCookie(cookie);

                // Antwort raus schicken.
                map.put("token: ", registerToken.toString());

                return ResponseEntity.badRequest().body(map);
            } else {
                registerRequest = userMapper.toUserRequest(registerToken);
            }
        }
        UserDto userDto = userService.createUser(registerRequest);
        var uri = uriBuilder.path("/{id}").buildAndExpand(userDto.getId()).toUri();
        return ResponseEntity.created(uri).body(userDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable UUID id) {
        var user = userService.getUserById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public Map<String, List<UserDto>> getAllUsers() {
        return userService.getAllUsers();
    }
}
