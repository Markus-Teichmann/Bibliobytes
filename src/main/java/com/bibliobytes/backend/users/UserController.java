package com.bibliobytes.backend.users;

import com.bibliobytes.backend.auth.config.JweConfig;
import com.bibliobytes.backend.email.MailServerConfig;
import com.bibliobytes.backend.email.MailService;
import com.bibliobytes.backend.users.dtos.ConfirmCodeRequest;
import com.bibliobytes.backend.users.dtos.RegisterUserRequest;
import com.bibliobytes.backend.users.dtos.UpdateRole;
import com.bibliobytes.backend.users.dtos.UserDto;
import com.bibliobytes.backend.users.entities.Role;
import com.bibliobytes.backend.auth.services.JweService;
import com.bibliobytes.backend.users.entities.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final JweConfig jwtConfig;
    private final JweService jweService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final MailServerConfig mailConfig;
    private final MailService mailService;
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
            // Generate Random Code
            IntStream stream = new Random().ints(6L, 0, 10);
            String code = Arrays.toString(stream.toArray());
            code = code.replaceAll("[^0-9]", "");

            // Email verschicken
//            mailService.sendSimpleMessage(
//                    mailConfig.getFrom(),
//                    user.getEmail(),
//                    "Registrierung bei Bibiliobytes",
//                    "Bitte gebe den Code: " + code + "auf der Website ein."
//            );
            System.out.println("Der Code für die Email lautet: " + code);

            // Create Token
            var registerToken = jweService.generateRegisterRequestToken(request, code);

            // Place Token in Response
            var cookie = new Cookie("register_request_token", registerToken.toString());
            cookie.setHttpOnly(true);
            cookie.setPath("/users");
            cookie.setMaxAge((int) jwtConfig.getRegisterRequestTokenExpiration());
            cookie.setSecure(true);
            response.addCookie(cookie);

            return ResponseEntity.ok().body(
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

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmCode(
        @Valid @RequestBody ConfirmCodeRequest codeRequest,
        @CookieValue(value = "register_request_token") String token,
        UriComponentsBuilder uriBuilder
    ) {
        var jwt = jweService.parse(token);
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
        }
        user.setPassword(passwordEncoder.encode(jwt.get("password", String.class)));
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

    @GetMapping("/applicants")
    public ResponseEntity<List<UserDto>> getApplicants() {
        var applicants = userRepository.findAllByRole(Role.APPLICANT)
                .stream().map(a -> userMapper.toDto(a)).toList();
        return ResponseEntity.ok(applicants);
    }

    @PutMapping("/updateRole")
    public ResponseEntity<UserDto> updateRole(
        @Valid @RequestBody UpdateRole request
    ) {
        System.out.println("Test");
        var user = userRepository.findById(request.getId()).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        user.setRole(request.getRole());
        userRepository.save(user);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PutMapping("/updateEmail")
    public ResponseEntity<Void> updateEmail() {
        var user = userService.findMe();
        // ToDo: In der ConfirmCode Methode können wir über den aud claim bestimmen, welche Methode danach aufgerufen werden soll.
        // ToDo: Diese Methode sollten private sein und das Ganze Update zeug machen.
        return null;
    }

    @PutMapping("/updatePassword")
    public ResponseEntity<Void> updatePassword() {
        var user = userService.findMe();
        return null;
    }

    @PutMapping("/updateFirstName")
    public ResponseEntity<Void> updateFirstName() {
        var user = userService.findMe();
        return null;
    }

    @PutMapping("/updateLastName")
    public ResponseEntity<Void> updateLastName() {
        var user = userService.findMe();
        return null;
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
