package com.bibliobytes.backend.controllers;

import com.bibliobytes.backend.rentals.RentalService;
import com.bibliobytes.backend.rentals.dtos.RentalDto;
import com.bibliobytes.backend.rentals.requests.UpdateRentalEndRequest;
import com.bibliobytes.backend.rentals.requests.UpdateRentalExternalRequest;
import com.bibliobytes.backend.rentals.requests.UpdateRentalStatusRequest;
import com.bibliobytes.backend.validation.ValidRentalId.ValidRentalId;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rentals")
@AllArgsConstructor
public class RentalController {
    private RentalService rentalService;

    @GetMapping("/{id}")
    public ResponseEntity<RentalDto> get(
            @PathVariable @ValidRentalId Long id
    ) {
        RentalDto rental = rentalService.getRental(id);
        return ResponseEntity.ok(rental);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<RentalDto> updateStatus(
            @PathVariable @ValidRentalId Long id,
            @Valid @RequestBody UpdateRentalStatusRequest request
    ) {
        RentalDto rental = rentalService.updateStatus(id, request);
        return ResponseEntity.ok(rental);
    }

    @PutMapping("/{id}/external")
    public ResponseEntity<RentalDto> updateExternal(
            @PathVariable @ValidRentalId Long id,
            @Valid @RequestBody UpdateRentalExternalRequest request
    ) {
        RentalDto rental = rentalService.updateExternal(id, request);
        return ResponseEntity.ok(rental);
    }

    @PutMapping("/{id}/end")
    public ResponseEntity<RentalDto> updateEnd(
            @PathVariable @ValidRentalId Long id,
            @Valid @RequestBody UpdateRentalEndRequest request
    ) {
        RentalDto rental = rentalService.updateEnd(id, request);
        return ResponseEntity.ok(rental);
    }
    
}
