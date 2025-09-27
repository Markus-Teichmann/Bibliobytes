package com.bibliobytes.backend.rentals.requests;

import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateRentalEndRequest {
    private LocalDate rentalEndDate;

    @AssertTrue(message = "End date required")
    private boolean notBlank() {
        return rentalEndDate != null;
    }
}
