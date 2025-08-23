package com.bibliobytes.backend.users;

import com.bibliobytes.backend.auth.config.JweConfig;
import com.bibliobytes.backend.auth.services.Jwe;
import com.bibliobytes.backend.users.dtos.*;
import com.bibliobytes.backend.users.dtos.RegisterUserRequest;
import com.bibliobytes.backend.users.dtos.UpdateCredentialsDto;
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
            Jwe token = jweService.generateRegisterUserToken(request);

            // Place Token in Response
            var cookie = new Cookie("register_token", token.toString());
            cookie.setHttpOnly(true);
            cookie.setPath("/users");
            cookie.setMaxAge((int) jweConfig.getRegisterUserTokenExpiration());
            cookie.setSecure(true);
            response.addCookie(cookie);

            return ResponseEntity.ok().body(
                    Map.of(
                            "token", token.toString(),
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

    @PostMapping("register/confirm")
    public ResponseEntity<?> confirmRegistrationData(
        @Valid @RequestBody RegisterCodeRequest codeRequest,
        @CookieValue(value = "register_token") String token,
        UriComponentsBuilder uriBuilder
    ) {
        Jwe jwe = jweService.parse(token);
        if (jwe == null || jwe.isExpired()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Token expired"));
        }
        if (!codeRequest.getCode().matches(jwe.getCode())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid code"));
        }
        RegisterUserRequest request = jwe.toDto();
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        User user = userService.registerUser(request);
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(uri).body(userMapper.toDto(user));
    }

    @PutMapping("/updateCredentials")
    public ResponseEntity<?> updateCredentials(
            @Valid @RequestBody UpdateCredentialsDto request,
            HttpServletResponse response
    ) throws Exception {
        var me = userService.findMe();
        if (me == null) {
            // Wird nie passieren, da wir bereits mind. User sein müssen um diese Methode aufrufen zu können. Siehe SecurityConfig
            return ResponseEntity.internalServerError().build();
        }
        if (me.getRole() != Role.ADMIN && request.getId() != null) {
            // Nicht Admins dürfen die Credentials von anderen nicht verändern!
            return ResponseEntity.badRequest().build();
        }
        if (me.getRole() == Role.ADMIN && request.getId() != null) {
            // Admins dürfen die Credentials von anderen Nutern ohne erneutes Prüfen verändern.
            // Für diesen Teil ist auch die Angabe des alten Passwords nicht notwendig.
            if (request.getNewPassword() != null) {
                request.setNewPassword(passwordEncoder.encode(request.getNewPassword()));
            }
            User other = userService.updateCredentials(request);
            if (other == null) {
                // Nutzer mit der im Request angegebenen Id wurde nicht gefunden.
                return ResponseEntity.notFound().build();
            } else {
                // Der veränderte Nutzer mit der im Request angegebenen Id wird ausgegeben.
                return ResponseEntity.ok(userMapper.toDto(other));
            }
        }
        // Hier haben wir schon geprüft, ob eine Id eines anderen Nutzers angegeben wurde. Somit muss die Id hier immer null sein, denn die anderen Fälle wurden schon behandelt.
        // Um die eigenen Daten zu verändern, muss das alte Passwort bekannt sein.
        if (request.getOldEmail() == null || request.getOldPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Email and Password are required"
            ));
        }

        // Angegebene Daten authentifizieren
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getOldEmail(),
                        request.getOldPassword()
                )
        );

        // Auf Tippfehler prüfen
        if(request.getNewPassword() != null && !request.getNewPassword().matches(request.getConfirmNewPassword())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Passwords do not match"));
        }
        if(request.getNewEmail() != null && !request.getNewEmail().matches(request.getConfirmNewEmail())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Emails do not match"));
        }

        // Generate Token -> JWEService -> MailService
        Jwe token = jweService.generateUpdateUserCredentialsToken(request);

        String message = "Wir haben Ihnen eine Email mit einem Bestätigungscode and ihre Emailadresse " + me.getEmail();
        if (request.getNewEmail() != null) {
            message += " und ihre neue Emailadresse " + request.getNewEmail();
        }
        message += " geschickt";

        // Place Token in Response
        var cookie = new Cookie("update_credentials_token", token.toString());
        cookie.setHttpOnly(true);
        cookie.setPath("/users");
        cookie.setMaxAge((int) jweConfig.getUpdateUserCredentialsTokenExpiration());
        cookie.setSecure(true);
        response.addCookie(cookie);

        return ResponseEntity.ok().body(
                Map.of(
                        "token", token.toString(),
                        "message", message
                )
        );
    }

    @PutMapping("/updateCredentials/confirm")
    public ResponseEntity<?> confirmUpdateCredentials(
            @Valid @RequestBody UpdateCodeRequest codeRequest,
            @CookieValue(value = "update_credentials_token") String token
    ) {
        Jwe jwe = jweService.parse(token);
        if (jwe == null || jwe.isExpired()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Token expired"));
        }
        String code = codeRequest.getCodeFromOldEmail();
        if (codeRequest.getCodeFromNewEmail() != null) {
            code += codeRequest.getCodeFromNewEmail();
        }
        if(!code.matches(jwe.getCode())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid code"));
        }
        UpdateCredentialsDto request = jwe.toDto();
        if (request.getNewPassword() != null) {
            request.setNewPassword(passwordEncoder.encode(request.getNewPassword()));
        }
        User user = userService.updateCredentials(request);
        return ResponseEntity.ok().body(userMapper.toDto(user));
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

    @GetMapping()
    public ResponseEntity<Map<String, List<UserDto>>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
