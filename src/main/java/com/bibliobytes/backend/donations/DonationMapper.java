package com.bibliobytes.backend.donations;

import com.bibliobytes.backend.donations.dtos.DonationDto;
import com.bibliobytes.backend.donations.entities.Donation;

public interface DonationMapper {
    Donation toEntity(DonationDto dto);
}
