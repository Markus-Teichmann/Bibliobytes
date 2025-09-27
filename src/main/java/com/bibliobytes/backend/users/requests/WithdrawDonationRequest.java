package com.bibliobytes.backend.users.requests;

import com.bibliobytes.backend.validation.validdonationid.ValidDonationId;
import lombok.Data;

@Data
public class WithdrawDonationRequest {
    @ValidDonationId
    Long donationId;
}
