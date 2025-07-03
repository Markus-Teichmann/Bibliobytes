package com.bibliobytes.backend.users;

import com.bibliobytes.backend.auth.config.JweConfig;
import com.bibliobytes.backend.users.dtos.*;
import com.bibliobytes.backend.users.entities.Role;
import com.bibliobytes.backend.auth.services.JweService;
import com.bibliobytes.backend.users.entities.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

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
            var confirmableToken = jweService.generateConfirmableToken(user.getEmail(), Map.of(
                    "firstName", request.getFirstName(),
                    "lastName", request.getLastName(),
                    "password", passwordEncoder.encode(request.getPassword()),
                    "type", RegisterUserRequest.class.getSimpleName()
            ));

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
        Object data = jweService.confirmedData(token, codeRequest.getCode());
        if (data == null) {return ResponseEntity.badRequest().build();}
        if (data instanceof Map) {return ResponseEntity.badRequest().body(data);}
        User user = null;
        if (data instanceof RegisterUserRequest request) {
            user = userService.registerUser(request);
        }
        if (data instanceof UpdateCredentialsDto request) {
            user = userService.updateCredentials(request);
        }
        if (user != null) {
            var uri = uriBuilder.path("/users/{id}").buildAndExpand(user.getId()).toUri();
            return ResponseEntity.created(uri).body(userMapper.toDto(user));
        }
        return ResponseEntity.internalServerError().build();
        // Ab hier sind die Daten entschlüsselt, der Code passt und sie wurden validiert,
        // d.h. ab hier können wir diesen Daten vertrauen ähnlich wäre es, wenn sie aus

//        var jwe = jweService.parse(token);
//        if (jwe == null || jwe.isExpired()) {
//            return ResponseEntity.badRequest().body(
//                    Map.of("message", "Token expired")
//            );
//        }
//        if (!codeRequest.getCode().matches(jwe.get("code", String.class))) {
//            return ResponseEntity.badRequest().body(
//                    Map.of("message", "Invalid code")
//            );
//        }


        //RegisterUserRequest request = tokenMapper.toRegisterUserRequest(jwe.getClaims());

        // @RequestBody @Valid entsprungen wären.

//        var user = userRepository.findByEmail(request.getEmail()).orElse(null);
//        if (user == null) {
//            user = userMapper.toEntity(request);
//        }
//        user.setPassword(passwordEncoder.encode(request.getPassword()));
//        userRepository.save(user);
//        var uri = uriBuilder.path("/users/{id}").buildAndExpand(user.getId()).toUri();
//        return ResponseEntity.created(uri).body(userMapper.toDto(user));
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
        if (user.getRole() == Role.ADMIN && request.getId() != null) {
            user = userService.updateCredentials(request);
            if (user == null) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(userMapper.toDto(user));
            }
        } else {
            var confirmableToken = jweService.generateConfirmableToken(request.getEmail(), Map.of(
                    "id", request.getId(),
                    "password", passwordEncoder.encode(request.getPassword())
            ));
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
    }

    @PutMapping("/updateProfile")
    public ResponseEntity<UserDto> updateProfile(
            @Valid @RequestBody UpdateProfileDto request
    ) {
        return null;
    }

    @PutMapping("/updateRole")
    public ResponseEntity<UserDto> updateRole(
        @Valid @RequestBody UpdateRole request
    ) {
        var user = userRepository.findById(request.getId()).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        user.setRole(request.getRole());
        userRepository.save(user);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteUser() {
        var user = userService.findMe();
        return null;
    }

    @GetMapping()
    public Map<String, List<UserDto>> getAllUsers() {
        return userService.getAllUsers();
    }
}
