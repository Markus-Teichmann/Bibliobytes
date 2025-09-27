package com.bibliobytes.backend.rentals.requests;

import com.bibliobytes.backend.rentals.entities.RentalState;
import com.bibliobytes.backend.validation.validrentalstate.ValidRentalState;
import lombok.Data;

@Data
public class UpdateRentalStatusRequest {
    @ValidRentalState
    private RentalState state;
}
