package com.bibliobytes.backend.controllers;

import com.bibliobytes.backend.donations.DonationService;
import com.bibliobytes.backend.donations.dtos.*;
import com.bibliobytes.backend.donations.requests.UpdateConditionRequest;
import com.bibliobytes.backend.donations.requests.UpdateDonationStatusRequest;
import com.bibliobytes.backend.donations.requests.UpdateItemRequest;
import com.bibliobytes.backend.donations.requests.UpdateOwnerRequest;
import com.bibliobytes.backend.validation.validdonationid.ValidDonationId;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/donations")
@AllArgsConstructor
public class DonationController {
    private final DonationService donationService;

    @GetMapping("/{id}")
    public ResponseEntity<DonationDto> getDonationById(
            @PathVariable @ValidDonationId Long id
    ) {
        DonationDto donation = donationService.getDonationBy(id);
        return ResponseEntity.ok(donation);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<DonationDto> updateStatus(
            @PathVariable @ValidDonationId Long id,
            @Valid @RequestBody UpdateDonationStatusRequest request
    ) {
        DonationDto donation = donationService.updateDonationStatus(id, request);
        return ResponseEntity.ok(donation);
    }

    @PutMapping("/{id}/item")
    public ResponseEntity<DonationDto> updateItem(
            @PathVariable @ValidDonationId Long id,
            @Valid @RequestBody UpdateItemRequest request
    ) {
        DonationDto donation = donationService.updateItem(id, request);
        return ResponseEntity.ok(donation);
    }

    @PutMapping("/{id}/owner")
    public ResponseEntity<DonationDto> updateOwner(
            @PathVariable @ValidDonationId Long id,
            @Valid @RequestBody UpdateOwnerRequest request
    ) {
        DonationDto donation = donationService.updateOwner(id, request);
        return ResponseEntity.ok(donation);
    }

    @PutMapping("/{id}/condition")
    public ResponseEntity<DonationDto> updateCondition(
            @PathVariable @ValidDonationId Long id,
            @Valid @RequestBody UpdateConditionRequest request
    ) {
        DonationDto donation = donationService.updateCondition(id, request);
        return ResponseEntity.ok(donation);
    }
}
