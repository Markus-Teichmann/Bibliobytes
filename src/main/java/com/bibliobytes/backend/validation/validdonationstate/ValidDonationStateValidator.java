package com.bibliobytes.backend.validation.validdonationstate;

import com.bibliobytes.backend.donations.entities.DonationState;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidDonationStateValidator implements ConstraintValidator<ValidDonationState, DonationState> {

    @Override
    public boolean isValid(DonationState state, ConstraintValidatorContext context) {
        for (DonationState donationState: DonationState.values()) {
            if (donationState.equals(state)) {
                return true;
            }
        }
        return false;
    }
}
