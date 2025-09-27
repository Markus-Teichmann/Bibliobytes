package com.bibliobytes.backend.donations.requests;

import com.bibliobytes.backend.donations.entities.DonationState;
import com.bibliobytes.backend.validation.validdonationstate.ValidDonationState;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateDonationStatusRequest {
    @ValidDonationState
    @NotBlank(message = "New State is required.")
    private DonationState state;
}
