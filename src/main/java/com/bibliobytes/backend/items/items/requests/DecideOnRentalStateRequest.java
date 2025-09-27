package com.bibliobytes.backend.items.items.requests;

import com.bibliobytes.backend.rentals.entities.RentalState;
import com.bibliobytes.backend.validation.ValidRentalId.ValidRentalId;
import com.bibliobytes.backend.validation.validrentalstate.ValidRentalState;
import lombok.Data;

@Data
public class DecideOnRentalStateRequest {
    @ValidRentalId
    private long rentalId;
    @ValidRentalState
    private RentalState state;
}
