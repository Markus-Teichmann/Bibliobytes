package com.bibliobytes.backend.items.items.requests;

import com.bibliobytes.backend.validation.lowercase.Lowercase;
import com.bibliobytes.backend.validation.validdonationid.ValidDonationId;
import com.bibliobytes.backend.validation.validexternalid.ValidExternalId;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class RentItemRequest {
    @ValidDonationId(message = "Donation not Found")
    private Long donationId;
    private LocalDate startDate;
    private LocalDate endDate;
    @ValidExternalId(message = "External not Found")
    private UUID externalId;
    private String externalFirstName;
    private String externalLastName;
    @Email
    @Lowercase
    private String externalEmail;

    @AssertTrue(message = "External Data required")
    private boolean requiredDataForExternal() {
        if (
                externalId == null &&
                externalFirstName == null &&
                externalLastName == null &&
                externalEmail == null
        ) {
            return true;
        }
        if (externalId != null) {
            return externalFirstName == null && externalLastName == null && externalEmail == null;
        }
        return externalFirstName != null && externalLastName != null && externalEmail != null;
    }

    public boolean registerNewExternal() {
        return externalId == null && externalFirstName != null && externalLastName != null && externalEmail != null;
    }

    public boolean forExternalUser() {
        return externalId != null && externalFirstName == null && externalLastName == null && externalEmail == null;
    }

    @AssertTrue(message = "End Date after Start Date")
    private boolean endDateAfterStartDate() {
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        if (endDate == null) {
            endDate = LocalDate.now().plusWeeks(4);
        }
        return startDate.isBefore(endDate);
    }
}
