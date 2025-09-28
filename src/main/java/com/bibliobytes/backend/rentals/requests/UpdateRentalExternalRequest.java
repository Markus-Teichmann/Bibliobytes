package com.bibliobytes.backend.rentals.requests;

import com.bibliobytes.backend.validation.lowercase.Lowercase;
import com.bibliobytes.backend.validation.validexternalid.ValidExternalId;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateRentalExternalRequest {
    @ValidExternalId
    private UUID userId;
    @Lowercase
    @Email
    private String email;
    private String firstName;
    private String lastName;

    @AssertTrue
    private boolean validRequest() {
        if (userId == null) {
            return email != null && firstName != null && lastName != null;
        } else {
            return email == null && firstName == null && lastName == null;
        }
    }
}
