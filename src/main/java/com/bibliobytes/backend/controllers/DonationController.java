package com.bibliobytes.backend.controllers;

import com.bibliobytes.backend.donations.DonationService;
import com.bibliobytes.backend.donations.dtos.*;
import com.bibliobytes.backend.donations.requests.UpdateConditionRequest;
import com.bibliobytes.backend.donations.requests.UpdateDonationStatusRequest;
import com.bibliobytes.backend.donations.requests.UpdateItemRequest;
import com.bibliobytes.backend.donations.requests.UpdateOwnerRequest;
import com.bibliobytes.backend.items.ItemService;
import com.bibliobytes.backend.items.ItemServiceDispatcher;
import com.bibliobytes.backend.validation.validdonationid.ValidDonationId;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/donations")
@AllArgsConstructor
public class DonationController {
    private final DonationService donationService;
    private ItemServiceDispatcher itemServiceDispatcher;

    @GetMapping("/{id}")
    public ResponseEntity<DonationDto> getDonationById(
            @PathVariable @ValidDonationId Long id
    ) {
        ItemService itemService = itemServiceDispatcher.dispatch(id);
        DonationDto donation = donationService.getDonationBy(id, itemService);
        return ResponseEntity.ok(donation);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<DonationDto> updateStatus(
            @PathVariable @ValidDonationId Long id,
            @Valid @RequestBody UpdateDonationStatusRequest request
    ) {
        ItemService itemService = itemServiceDispatcher.dispatch(id);
        DonationDto donation = donationService.updateDonationStatus(id, request, itemService);
        return ResponseEntity.ok(donation);
    }

    @PutMapping("/{id}/item")
    public ResponseEntity<DonationDto> updateItem(
            @PathVariable @ValidDonationId Long id,
            @Valid @RequestBody UpdateItemRequest request
    ) {
        ItemService itemService = itemServiceDispatcher.dispatch(id);
        DonationDto donation = donationService.updateItem(id, request, itemService);
        return ResponseEntity.ok(donation);
    }

    @PutMapping("/{id}/owner")
    public ResponseEntity<DonationDto> updateOwner(
            @PathVariable @ValidDonationId Long id,
            @Valid @RequestBody UpdateOwnerRequest request
    ) {
        ItemService itemService = itemServiceDispatcher.dispatch(id);
        DonationDto donation = donationService.updateOwner(id, request, itemService);
        return ResponseEntity.ok(donation);
    }

    @PutMapping("/{id}/condition")
    public ResponseEntity<DonationDto> updateCondition(
            @PathVariable @ValidDonationId Long id,
            @Valid @RequestBody UpdateConditionRequest request
    ) {
        ItemService itemService = itemServiceDispatcher.dispatch(id);
        DonationDto donation = donationService.updateCondition(id, request, itemService);
        return ResponseEntity.ok(donation);
    }
}
