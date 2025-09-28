package com.bibliobytes.backend.controllers;

import com.bibliobytes.backend.auth.services.jwe.Jwe;
import com.bibliobytes.backend.auth.services.jwe.JweService;
import com.bibliobytes.backend.donations.DonationService;
import com.bibliobytes.backend.donations.dtos.DonationDto;
import com.bibliobytes.backend.items.items.ItemServiceUtils;
import com.bibliobytes.backend.rentals.RentalService;
import com.bibliobytes.backend.users.requests.WithdrawDonationRequest;
import com.bibliobytes.backend.donations.entities.Donation;
import com.bibliobytes.backend.rentals.dtos.RentalDto;
import com.bibliobytes.backend.users.UserMapper;
import com.bibliobytes.backend.users.UserNotFoundException;
import com.bibliobytes.backend.users.UserService;
import com.bibliobytes.backend.users.requests.ConfirmationCodeRequest;
import com.bibliobytes.backend.users.dtos.*;
import com.bibliobytes.backend.users.entities.User;
import com.bibliobytes.backend.users.requests.*;
import com.bibliobytes.backend.validation.notexpired.NotExpired;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/me")
@AllArgsConstructor
public class MeController {
    private final DonationService donationService;
    private final UserService userService;
    private final JweService jweService;
    private final RentalService rentalService;
    private ItemServiceUtils itemServiceUtils;

    @GetMapping()
    public ResponseEntity<UserDto> getMe(){
        UserDto me = userService.findMe();
        return ResponseEntity.ok(me);
    }

    @PutMapping("/firstname")
    public ResponseEntity<UserDto> updateFirstName(
            @RequestBody @Valid UpdateFirstNameRequest request
    ) {
        UserDto me = userService.updateFirstName(request);
        if (me == null) {
            throw new UserNotFoundException();
        }
        return ResponseEntity.ok().body(me);
    }

    @PutMapping("/lastname")
    public ResponseEntity<UserDto> updateLastName(
            @RequestBody @Valid UpdateLastNameRequest request
    ) {
        UserDto me = userService.updateLastName(request);
        if (me == null) {
            throw new UserNotFoundException();
        }
        return ResponseEntity.ok(me);
    }

    @PostMapping("/email")
    public ResponseEntity<Map<String, String>> updateEmail(
            @RequestBody @Valid UpdateEmailRequest request,
            HttpServletResponse response
    ) throws Exception {
        Cookie cookie = userService.generateUpdateEmailCookie(request, jweService);
        response.addCookie(cookie);
        return ResponseEntity.ok(Map.of(
                "message", "Wir haben Ihnen eine Email mit einem Bestätigungscode and ihre Emailadresse "
                        + request.getOldEmail()
                        + "und ihre neue Emailadresse "
                        + request.getNewEmail()
                        + " geschickt."
        ));
    }

    @PutMapping("/email")
    public ResponseEntity<?> updateEmail(
            @RequestBody @Valid ConfirmationCodeRequest confirm,
            @CookieValue(value = "update_email_token") @NotExpired String token
    ) {
        String code = confirm.getCodeFromOldEmail() + confirm.getCodeFromNewEmail();

        Jwe jwe = jweService.parse(token);
        if (!code.matches(jwe.getCode())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid Code."));
        }
        UserDto user = userService.updateEmail(jwe);
        return ResponseEntity.ok().body(user);
    }

    @PostMapping("/password")
    public ResponseEntity<Map<String, String>> updatePassword(
            @RequestBody @Valid UpdatePasswordRequest request,
            HttpServletResponse response
    ) throws Exception {
        Cookie cookie = userService.generateUpdatePasswordCookie(request, jweService);
        response.addCookie(cookie);
        return ResponseEntity.ok(Map.of(
                "message", "Wir haben Ihnen eine Email mit einem Bestätigungscode geschickt"
        ));
    }

    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(
            @RequestBody @Valid ConfirmationCodeRequest confirm,
            @CookieValue(value = "update_password_token") @NotExpired String token
    ) {
        String code = confirm.getCodeFromOldEmail();
        Jwe jwe = jweService.parse(token);
        if (!code.matches(jwe.getCode())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid Code."));
        }
        UserDto user = userService.updatePassword(jwe);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/donations")
    public ResponseEntity<Set<DonationDto>> getMyDonations() {
        UUID myId = userService.getMyId();
        return ResponseEntity.ok(donationService.getAllDonations(myId, itemServiceUtils));
    }

    @PutMapping("/donations")
    public ResponseEntity<?> withdrawDonation(
            @RequestBody @Valid WithdrawDonationRequest request
    ) {
        UUID myId = userService.getMyId();
        donationService.withdrawDonation(myId, request, itemServiceUtils);
//        DonationDto donation =
//        if (donation == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Donation not found."));
//        }
        return ResponseEntity.ok(donationService.getAllDonations(myId, itemServiceUtils));
    }

    @GetMapping("/rentals")
    public ResponseEntity<Set<RentalDto>> getMyRentals() {
        Set<RentalDto> rentals = userService.getRentals(rentalService, itemServiceUtils);
        return ResponseEntity.ok().body(rentals);
    }

    @DeleteMapping
    public ResponseEntity<UserDto> delete() {
        UserDto user = userService.delete();
        return ResponseEntity.ok().body(user);
    }
}
