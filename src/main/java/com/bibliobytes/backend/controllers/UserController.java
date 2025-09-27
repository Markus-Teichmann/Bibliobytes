package com.bibliobytes.backend.controllers;

import com.bibliobytes.backend.auth.dtos.JweResponse;
import com.bibliobytes.backend.users.requests.*;
import com.bibliobytes.backend.auth.services.jwe.Jwe;
import com.bibliobytes.backend.donations.DonationService;
import com.bibliobytes.backend.donations.dtos.DonationDto;
import com.bibliobytes.backend.users.requests.WithdrawDonationRequest;
import com.bibliobytes.backend.donations.entities.Donation;
import com.bibliobytes.backend.rentals.dtos.RentalDto;
import com.bibliobytes.backend.users.UserMapper;
import com.bibliobytes.backend.users.UserRepository;
import com.bibliobytes.backend.users.UserService;
import com.bibliobytes.backend.users.dtos.*;
import com.bibliobytes.backend.users.entities.Role;
import com.bibliobytes.backend.auth.services.jwe.JweService;
import com.bibliobytes.backend.users.entities.User;
import com.bibliobytes.backend.validation.notexpired.NotExpired;
import com.bibliobytes.backend.validation.validuserid.ValidUserId;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final JweService jweService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final DonationService donationService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody RegisterUserRequest request,
            HttpServletResponse response,
            UriComponentsBuilder uriBuilder
    ) throws Exception {
        if (request.registerExternal()) {
            User external = userService.registerExternal(request);
            var uri = uriBuilder.path("/users/{id}").buildAndExpand(external.getId()).toUri();
            return ResponseEntity.created(uri).body(userMapper.toDto(external));
        }
        Cookie cookie = userService.generateRegisterCookie(request);
        response.addCookie(cookie);
        return ResponseEntity.ok().body(
                Map.of("message", "Wir haben Ihnen einen Bestätigungscode an "
                + request.getEmail() + " geschickt.")
        );
//        var user = userRepository.findByEmail(request.getEmail()).orElse(null);
//        if (!(user == null || (user.getRole() == Role.EXTERNAL && request.getPassword() != null))) {
//            return ResponseEntity.badRequest().body(
//                    Map.of("email", "Email is allready registered.")
//            );
//        }
//        if (user == null) {
//            user = userMapper.toEntity(request);
//        }
//        if (request.getPassword() != null) {
//            // Create Token
//            Jwe token = jweService.generateRegisterUserToken(request);
//
//            // Place Token in Response
//            var cookie = new Cookie("register_token", token.toString());
//            cookie.setHttpOnly(true);
//            cookie.setPath("/users");
//            cookie.setMaxAge((int) jweConfig.getRegisterUserTokenExpiration());
//            cookie.setSecure(true);
//            response.addCookie(cookie);
//
//            return ResponseEntity.ok().body(
//                    Map.of(
//                            "token", token.toString(),
//                            "message", "Wir haben Ihnen eine Email mit einem Bestätigungscode and ihre Emailadresse " +
//                                    user.getEmail() + " geschickt."
//                    )
//            );
//        }
//        user.setRole(Role.EXTERNAL);
//        userRepository.save(user);

    }

    @PostMapping("/register/confirm")
    public ResponseEntity<?> confirmRegistrationData(
        @Valid @RequestBody RegisterCodeRequest codeRequest,
        @CookieValue(value = "register_token") @NotExpired String token,
        UriComponentsBuilder uriBuilder
    ) {
        Jwe jwe = jweService.parse(token);
        if (!codeRequest.getCode().matches(jwe.getCode())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid code"));
        }
        RegisterUserRequest request = jwe.toDto();
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        User user = userService.registerUser(request);
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(uri).body(userMapper.toDto(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) throws Exception {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        Cookie refreshCookie = userService.generateRefreshCookie(user);
        response.addCookie(refreshCookie);
        JweResponse jweResponse = userService.generateJweResponse(user);
        return ResponseEntity.ok(jweResponse);

//        if (user == null || user.getRole() == Role.EXTERNAL) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
//                    Map.of("message", "Sie müssen sich zuerst registrieren.")
//            );
//        }
//        if (user.getRole() == Role.APPLICANT) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
//                    Map.of("message", "Sie müssen noch von einem Admin freigeschaltet werden.")
//            );
//        }

//        var accessToken = jweService.generateAccessToken(tokenMapper.toAccessTokenDto(user));
//        var refreshToken = jweService.generateRefreshToken(tokenMapper.toRefreshTokenDto(user));
//
//        var cookie = new Cookie("refresh_token", refreshToken.toString());
//        cookie.setHttpOnly(true);
//        cookie.setPath("/auth");
//        cookie.setMaxAge((int) config.getRefreshTokenExpiration());//7 Tage
//        cookie.setSecure(true);
//        response.addCookie(cookie);
//        return ResponseEntity.ok(new JweResponse(accessToken.toString()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JweResponse> refreshToken(
            @CookieValue(value = "refresh_token") @NotExpired String refreshToken // value = "name_des_cookies" vgl. login
    ) throws Exception {
        JweResponse response = userService.generateJweResponse(refreshToken);
        return ResponseEntity.ok(response);
    }

    @GetMapping()
    public ResponseEntity<Set<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable @ValidUserId UUID id) {
        var user = userRepository.findById(id).orElse(null);
//        if (user == null) {
//            return ResponseEntity.notFound().build();
//        }
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PutMapping("/{id}/firstname")
    public ResponseEntity<?> updateFirstName(
        @PathVariable @ValidUserId UUID id,
        @Valid @RequestBody UpdateFirstNameRequest request
    ) {
        User user = userService.updateFirstName(id, request);
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
//                    "message", "User with id" + id + " not found."
//            ));
//        }
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PutMapping("/{id}/lastname")
    public ResponseEntity<?> updateLastName(
            @PathVariable @ValidUserId UUID id,
            @Valid @RequestBody UpdateLastNameRequest request
    ) {
        User user = userService.updateLastName(id, request);
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
//                    "message", "User with id" + id + " not found."
//            ));
//        }
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PutMapping("/{id}/email")
    public ResponseEntity<?> updateEmail(
            @PathVariable @ValidUserId UUID id,
            @Valid @RequestBody UpdateEmailRequest request
    ) {
        User user = userService.updateEmail(id, request);
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
//                    "message", "User with id" + id + " not found."
//            ));
//        }
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<?> updatePassword(
            @PathVariable @ValidUserId UUID id,
            @Valid @RequestBody UpdatePasswordRequest request
    ) {
        request.setNewPassword(passwordEncoder.encode(request.getNewPassword()));
        User user = userService.updatePassword(id, request);
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
//                    "message", "User with id" + id + " not found."
//            ));
//        }
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @GetMapping("/{id}/donations")
    public ResponseEntity<Set<DonationDto>> getDonations(
            @PathVariable @ValidUserId UUID id
    ) {
        return ResponseEntity.ok(donationService.getAllDonations(id));
    }

    @PutMapping("/{id}/donations")
    public ResponseEntity<?> withdrawDonation(
            @PathVariable @ValidUserId UUID id,
            @Valid @RequestBody WithdrawDonationRequest request
    ) {
        Donation donation = donationService.withdrawDonation(id, request);
        if (donation == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Donation not found."));
        }
        return ResponseEntity.ok(donationService.getAllDonations(id));
    }

    @GetMapping("/{id}/rentals")
    public ResponseEntity<Set<RentalDto>> getRentals(
            @PathVariable @ValidUserId UUID id
    ) {
        Set<RentalDto> rentals = userService.getRentals(id);
        return ResponseEntity.ok(rentals);
    }

    @GetMapping("/search")
    public ResponseEntity<Set<UserDto>> searchUsers(
            @Valid @RequestBody SearchRequest request
    ) {
        //ToDo: Searching still not done
        return null;
    }

    @GetMapping("/new")
    public ResponseEntity<Set<UserDto>> getApplicants() {
        var applicants = userRepository.findAllByRole(Role.APPLICANT)
                .stream().map(a -> userMapper.toDto(a)).collect(Collectors.toSet());
        return ResponseEntity.ok(applicants);
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateRole(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateRoleRequest request
    ) {
        User user = userService.updateRole(id, request);
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
//                    "message", "User with id" + id + " not found."
//            ));
//        }
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @DeleteMapping()
    public ResponseEntity<UserDto> deleteUser(
            @RequestBody(required = false) DelteUserRequest request
    ) {
        User user = userService.deleteUser();
//        if (user == null) {
//            return ResponseEntity.notFound().build();
//        }
        return ResponseEntity.ok().body(userMapper.toDto(user));
    }

//    @ExceptionHandler(BadCredentialsException.class)
//    public ResponseEntity<Void> handleUserNotFound() {
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//    }


    /*
        Diese Methode kann nur von Admins ausgeführt werden und
        diese Methode soll das Profil eines anderen Users ändern
     */
//    @PutMapping("/{id}/profile")
//    public ResponseEntity<?> updateProfile(
//            @PathVariable UUID id,
//            @Valid @RequestBody UpdateProfileRequest request
//    ) {
//        User user = userService.updateProfile(id, request);
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
//                    "message", "User with id" + id + " not found."
//            ));
//        }
//        return ResponseEntity.ok(userMapper.toDto(user));

//    }
    /*
        Diese Methode kann nur von Admins ausgeführt werden und
        diese Methode soll die Email eines anderen Users ändern
     */

//    @PutMapping("/{id}/email")
//    public ResponseEntity<?> updateCredentials(
//            @PathVariable UUID id,
//            @Valid @RequestBody UpdateEmailRequest request
//    ) throws Exception {
//        User user = userService.updateCredentials(id, request);
//
//        var me = userService.findMe();
//        if (me == null) {
//            // Wird nie passieren, da wir bereits mind. User sein müssen um diese Methode aufrufen zu können. Siehe SecurityConfig
//            return ResponseEntity.internalServerError().build();
//        }
//        if (me.getRole() != Role.ADMIN && request.getId() != null) {
//            // Nicht Admins dürfen die Credentials von anderen nicht verändern!
//            return ResponseEntity.badRequest().build();
//        }
//        if (me.getRole() == Role.ADMIN && request.getId() != null) {
//            // Admins dürfen die Credentials von anderen Nutzern ohne erneutes Prüfen verändern.
//            // Für diesen Teil ist auch die Angabe des alten Passwords nicht notwendig.
//            if (request.getNewPassword() != null) {
//                request.setNewPassword(passwordEncoder.encode(request.getNewPassword()));
//            }
//            User other = userService.updateCredentials(request);
//            if (other == null) {
//                // Nutzer mit der im Request angegebenen Id wurde nicht gefunden.
//                return ResponseEntity.notFound().build();
//            } else {
//                // Der veränderte Nutzer mit der im Request angegebenen Id wird ausgegeben.
//                return ResponseEntity.ok(userMapper.toDto(other));
//            }
//        }
//        // Hier haben wir schon geprüft, ob eine Id eines anderen Nutzers angegeben wurde. Somit muss die Id hier immer null sein, denn die anderen Fälle wurden schon behandelt.
//        // Um die eigenen Daten zu verändern, muss das alte Passwort bekannt sein.
//        if (request.getOldEmail() == null || request.getOldPassword() == null) {
//            return ResponseEntity.badRequest().body(Map.of(
//                    "message", "Email and Password are required"
//            ));
//        }
//
//        // Angegebene Daten authentifizieren
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        request.getOldEmail(),
//                        request.getOldPassword()
//                )
//        );
//
//        // Auf Tippfehler prüfen
//        if(request.getNewPassword() != null && !request.getNewPassword().matches(request.getConfirmNewPassword())) {
//            return ResponseEntity.badRequest().body(Map.of("message", "Passwords do not match"));
//        }
//        if(request.getNewEmail() != null && !request.getNewEmail().matches(request.getConfirmNewEmail())) {
//            return ResponseEntity.badRequest().body(Map.of("message", "Emails do not match"));
//        }
//
//        // Generate Token -> JWEService -> MailService
//        Jwe token = jweService.generateUpdateUserCredentialsToken(request);
//
//        String message = "Wir haben Ihnen eine Email mit einem Bestätigungscode and ihre Emailadresse " + me.getEmail();
//        if (request.getNewEmail() != null) {
//            message += " und ihre neue Emailadresse " + request.getNewEmail();
//        }
//        message += " geschickt";
//
//        // Place Token in Response
//        var cookie = new Cookie("update_credentials_token", token.toString());
//        cookie.setHttpOnly(true);
//        cookie.setPath("/users");
//        cookie.setMaxAge((int) jweConfig.getUpdateUserCredentialsTokenExpiration());
//        cookie.setSecure(true);
//        response.addCookie(cookie);
//
//        return ResponseEntity.ok().body(
//                Map.of(
//                        "token", token.toString(),
//                        "message", message
//                )
//        );
//    }
//        @PutMapping("/update/credentials/confirm")
//    public ResponseEntity<?> confirmUpdateCredentials(
//            @Valid @RequestBody ConfirmationCodeRequest codeRequest,
//            @CookieValue(value = "update_credentials_token") String token
//    ) {
//        System.out.println("Test");
//        Jwe jwe = jweService.parse(token);
//        if (jwe == null || jwe.isExpired()) {
//            return ResponseEntity.badRequest().body(Map.of("message", "Token expired"));
//        }
//        String code = codeRequest.getCodeFromOldEmail();
//        if (codeRequest.getCodeFromNewEmail() != null) {
//            code += codeRequest.getCodeFromNewEmail();
//        }
//        if(!code.matches(jwe.getCode())) {
//            return ResponseEntity.badRequest().body(Map.of("message", "Invalid code"));
//        }
//        UpdateCredentialsRequest request = jwe.toDto();
//        if (request.getNewPassword() != null) {
//            request.setNewPassword(passwordEncoder.encode(request.getNewPassword()));
//        }
//        User user = userService.updateCredentials(request);
//        return ResponseEntity.ok().body(userMapper.toDto(user));
//    }


}
