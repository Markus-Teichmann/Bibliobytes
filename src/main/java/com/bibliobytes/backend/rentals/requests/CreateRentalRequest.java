package com.bibliobytes.backend.rentals.requests;

import com.bibliobytes.backend.validation.validdonationid.ValidDonationId;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

@Data
public class CreateRentalRequest {
    @ValidDonationId()
    private long donationId;
    private Date startDate;
    private Date endDate;
    private String externalFirstName;
    private String externalLastName;
    private String externalEmail;

    @AssertTrue()
    private boolean validExternal() {
        if (externalFirstName != null || externalLastName != null || externalEmail != null) {
            return (externalFirstName != null && externalLastName != null && externalEmail != null);
        }
        return true;
    }

    public boolean external() {
        return externalFirstName != null && externalLastName != null && externalEmail != null;
    }
}
