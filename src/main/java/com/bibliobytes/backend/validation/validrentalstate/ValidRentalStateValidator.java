package com.bibliobytes.backend.validation.validrentalstate;

import com.bibliobytes.backend.rentals.entities.RentalState;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidRentalStateValidator implements ConstraintValidator<ValidRentalState, RentalState> {

    @Override
    public boolean isValid(RentalState state, ConstraintValidatorContext context) {
        for (RentalState rentalState: RentalState.values()) {
            if (rentalState.equals(state)) {
                return true;
            }
        }
        return false;
    }
}
