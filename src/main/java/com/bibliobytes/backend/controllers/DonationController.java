package com.bibliobytes.backend.controllers;

import com.bibliobytes.backend.donations.DonationService;
import com.bibliobytes.backend.donations.dtos.*;
import com.bibliobytes.backend.donations.entities.DonationState;
import com.bibliobytes.backend.donations.requests.UpdateConditionRequest;
import com.bibliobytes.backend.donations.requests.UpdateDonationStatusRequest;
import com.bibliobytes.backend.donations.requests.UpdateItemRequest;
import com.bibliobytes.backend.donations.requests.UpdateOwnerRequest;
import com.bibliobytes.backend.items.ItemService;
import com.bibliobytes.backend.items.ItemServiceDispatcher;
import com.bibliobytes.backend.validation.validdonationid.ValidDonationId;
import com.bibliobytes.backend.validation.validdonationstate.ValidDonationState;
import com.bibliobytes.backend.validation.validitemid.ValidItemId;
import com.bibliobytes.backend.validation.validuserrolename.ValidUserRoleName;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Validated
@RestController
@RequestMapping("/donations")
@AllArgsConstructor
public class DonationController {
    private final DonationService donationService;
    private ItemServiceDispatcher itemServiceDispatcher;

    @GetMapping(params="donationState")
    public ResponseEntity<Set<DonationDto>> getAllDonationsByState(
            @RequestParam @ValidDonationState DonationState donationState
    ) {
        Set<DonationDto> donations = donationService.getDonationsBy(donationState);
        return ResponseEntity.ok(donations);
    }

    @GetMapping(params="itemId")
    public ResponseEntity<Set<DonationDto>> getAllDonationsByItemId(
            @RequestParam @ValidItemId Long itemId
    ) {
        Set<DonationDto> donations = donationService.getDonationsByItemId(itemId);
        return ResponseEntity.ok(donations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DonationDto> getDonationById(
            @PathVariable @ValidDonationId Long id
    ) {
        DonationDto donation = donationService.getDonationByDonationId(id);
        return ResponseEntity.ok(donation);
    }

    @PutMapping("/{donationId}/status")
    public ResponseEntity<DonationDto> updateStatus(
            @PathVariable @ValidDonationId Long donationId,
            @Valid @RequestBody UpdateDonationStatusRequest request
    ) {
        DonationDto donation = donationService.updateDonationStatus(donationId, request);
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
