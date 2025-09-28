package com.bibliobytes.backend.controllers;

import com.bibliobytes.backend.items.ItemService;
import com.bibliobytes.backend.items.ItemServiceDispatcher;
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
    private ItemServiceDispatcher itemServiceDispatcher;

    @GetMapping("/{id}")
    public ResponseEntity<RentalDto> get(
            @PathVariable @ValidRentalId Long id
    ) {
        ItemService itemService = itemServiceDispatcher.dispatch(id);
        RentalDto rental = rentalService.getRental(id, itemService);
        return ResponseEntity.ok(rental);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<RentalDto> updateStatus(
            @PathVariable @ValidRentalId Long id,
            @Valid @RequestBody UpdateRentalStatusRequest request
    ) {
        ItemService itemService = itemServiceDispatcher.dispatch(id);
        RentalDto rental = rentalService.updateStatus(id, request, itemService);
        return ResponseEntity.ok(rental);
    }

    @PutMapping("/{id}/external")
    public ResponseEntity<RentalDto> updateExternal(
            @PathVariable @ValidRentalId Long id,
            @Valid @RequestBody UpdateRentalExternalRequest request
    ) {
        ItemService itemService = itemServiceDispatcher.dispatch(id);
        RentalDto rental = rentalService.updateExternal(id, request, itemService);
        return ResponseEntity.ok(rental);
    }

    @PutMapping("/{id}/end")
    public ResponseEntity<RentalDto> updateEnd(
            @PathVariable @ValidRentalId Long id,
            @Valid @RequestBody UpdateRentalEndRequest request
    ) {
        ItemService itemService = itemServiceDispatcher.dispatch(id);
        RentalDto rental = rentalService.updateEnd(id, request, itemService);
        return ResponseEntity.ok(rental);
    }
    
}
