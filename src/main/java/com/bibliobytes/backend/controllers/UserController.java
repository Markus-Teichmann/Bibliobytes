package com.bibliobytes.backend.controllers;

import com.bibliobytes.backend.auth.dtos.JweResponse;
import com.bibliobytes.backend.auth.services.jwe.Jwe;
import com.bibliobytes.backend.auth.services.jwe.JweService;
import com.bibliobytes.backend.donations.DonationService;
import com.bibliobytes.backend.donations.dtos.DonationDto;
import com.bibliobytes.backend.items.items.ItemServiceUtils;
import com.bibliobytes.backend.rentals.RentalService;
import com.bibliobytes.backend.rentals.dtos.RentalDto;
import com.bibliobytes.backend.users.UserService;
import com.bibliobytes.backend.users.dtos.UserDto;
import com.bibliobytes.backend.users.requests.*;
import com.bibliobytes.backend.validation.notexpired.NotExpired;
import com.bibliobytes.backend.validation.validuserid.ValidUserId;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final JweService jweService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final DonationService donationService;
    private final RentalService rentalService;
    private ItemServiceUtils itemServiceUtils;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody RegisterUserRequest request,
            HttpServletResponse response,
            UriComponentsBuilder uriBuilder
    ) throws Exception {
        if (request.registerExternal()) {
            UserDto external = userService.registerExternal(request);
            var uri = uriBuilder.path("/users/{id}").buildAndExpand(external.getId()).toUri();
            return ResponseEntity.created(uri).body(external);
        }
        Cookie cookie = userService.generateRegisterCookie(request, jweService);
        response.addCookie(cookie);
        return ResponseEntity.ok().body(
                Map.of("message", "Wir haben Ihnen einen Bestätigungscode an "
                + request.getEmail() + " geschickt.")
        );
    }

    @PostMapping("/register/confirm")
    public ResponseEntity<?> confirmRegistrationData(
        @Valid @RequestBody RegisterCodeRequest codeRequest,
        @CookieValue(value = "register_token") @NotExpired String token,
        UriComponentsBuilder uriBuilder
    ) throws Exception {
        Jwe jwe = jweService.parse(token);
        if (!codeRequest.getCode().matches(jwe.getCode())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid code"));
        }
        RegisterUserRequest request = jwe.toDto();
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        UserDto user = userService.registerUser(request);
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(uri).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) throws Exception {
        Cookie refreshCookie = userService.generateRefreshCookie(request.getEmail(), jweService);
        response.addCookie(refreshCookie);
        JweResponse jweResponse = userService.generateJweResponse(request.getEmail(), jweService);
        return ResponseEntity.ok().body(jweResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JweResponse> refreshToken(
            @CookieValue(value = "refresh_token") @NotExpired String refreshToken // value = "name_des_cookies" vgl. login
    ) throws Exception {
        JweResponse response = userService.generateJweResponse(refreshToken, jweService);
        return ResponseEntity.ok(response);
    }

    @GetMapping()
    public ResponseEntity<Set<UserDto>> getAllUsers() {
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable @ValidUserId UUID id) {
        UserDto user = userService.getUser(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/firstname")
    public ResponseEntity<?> updateFirstName(
        @PathVariable @ValidUserId UUID id,
        @Valid @RequestBody UpdateFirstNameRequest request
    ) {
        UserDto user = userService.updateFirstName(id, request);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/lastname")
    public ResponseEntity<?> updateLastName(
            @PathVariable @ValidUserId UUID id,
            @Valid @RequestBody UpdateLastNameRequest request
    ) {
        UserDto user = userService.updateLastName(id, request);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/email")
    public ResponseEntity<?> updateEmail(
            @PathVariable @ValidUserId UUID id,
            @Valid @RequestBody UpdateEmailRequest request
    ) {
        UserDto user = userService.updateEmail(id, request);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<?> updatePassword(
            @PathVariable @ValidUserId UUID id,
            @Valid @RequestBody UpdatePasswordRequest request
    ) {
        request.setNewPassword(passwordEncoder.encode(request.getNewPassword()));
        UserDto user = userService.updatePassword(id, request);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}/donations")
    public ResponseEntity<Set<DonationDto>> getDonations(
            @PathVariable @ValidUserId UUID id
    ) {
        return ResponseEntity.ok(donationService.getAllDonations(id, itemServiceUtils));
    }

    @PutMapping("/{id}/donations")
    public ResponseEntity<?> withdrawDonation(
            @PathVariable @ValidUserId UUID id,
            @Valid @RequestBody WithdrawDonationRequest request
    ) {
        donationService.withdrawDonation(id, request, itemServiceUtils);
        return ResponseEntity.ok(donationService.getAllDonations(id, itemServiceUtils));
    }

    @GetMapping("/{id}/rentals")
    public ResponseEntity<Set<RentalDto>> getRentals(
            @PathVariable @ValidUserId UUID id
    ) {
        Set<RentalDto> rentals = userService.getRentals(id, rentalService, itemServiceUtils);
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
        Set<UserDto> applicants = userService.getApplicants();
        return ResponseEntity.ok(applicants);
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<UserDto> updateRole(
        @PathVariable @ValidUserId UUID id,
        @Valid @RequestBody UpdateRoleRequest request
    ) {
        UserDto user = userService.updateRole(id, request);
        System.out.println("UserDto: " + user);
        return ResponseEntity.ok().body(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserDto> deleteUser(
            @PathVariable @ValidUserId UUID id
    ) {
        UserDto user = userService.deleteUser(id);
        return ResponseEntity.ok().body(user);
    }
}