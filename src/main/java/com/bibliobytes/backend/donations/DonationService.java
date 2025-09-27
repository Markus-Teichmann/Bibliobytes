package com.bibliobytes.backend.donations;

import com.bibliobytes.backend.donations.dtos.*;
import com.bibliobytes.backend.donations.entities.Donation;
import com.bibliobytes.backend.donations.entities.DonationState;
import com.bibliobytes.backend.donations.requests.*;
import com.bibliobytes.backend.items.items.ItemServiceUtils;
import com.bibliobytes.backend.items.items.dtos.ItemDto;
import com.bibliobytes.backend.items.items.entities.Item;
import com.bibliobytes.backend.items.items.repositorys.ItemRepository;
import com.bibliobytes.backend.users.UserMapper;
import com.bibliobytes.backend.users.UserRepository;
import com.bibliobytes.backend.users.dtos.UserDto;
import com.bibliobytes.backend.users.entities.User;
import com.bibliobytes.backend.users.requests.WithdrawDonationRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class DonationService {
    private final ItemServiceUtils itemServiceUtils;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private DonationRepository donationRepository;
    private DonationMapper donationMapper;
    private UserMapper userMapper;

    public Set<DonationDto> getAllDonations(UUID ownerId) {
        return donationRepository.findAllByOwnerId(ownerId).stream()
                .map(donation -> toDto(donation))
                .collect(Collectors.toSet());
    }

    public DonationDto getDonationBy(Long id) {
        return donationRepository.findById(id).stream()
                .map(donation -> toDto(donation)).findFirst()
                .orElse(null);
    }

    public Donation withdrawDonation(UUID userId, WithdrawDonationRequest request) {
        Set<Long> donationIds = donationRepository.findAllDonationIdsByOwnerId(userId);
        if (!donationIds.contains(request.getDonationId())) {
            return null;
        }
        Donation donation = donationRepository.findById(request.getDonationId()).orElse(null);
        if (donation != null) {
            donation.setStatus(DonationState.WITHDRAWN);
            donationRepository.save(donation);
        }
        return donation;
    }

    public DonationDto updateDonationStatus(Long id, UpdateDonationStatusRequest request) {
        Donation donation = donationRepository.findById(id).orElse(null);
        donation.setStatus(request.getState());
        donationRepository.save(donation);
        return toDto(donation);
    }

    public DonationDto updateItem(Long id, UpdateItemRequest request) {
        Donation donation = donationRepository.findById(id).orElse(null);
        Item item = itemRepository.findById(request.getItemId()).orElse(null);
        donation.setItem(item);
        donationRepository.save(donation);
        return toDto(donation);
    }

    public DonationDto updateOwner(Long id, UpdateOwnerRequest request) {
        Donation donation = donationRepository.findById(id).orElse(null);
        User owner = userRepository.findById(request.getOwnerId()).orElse(null);
        donation.setOwner(owner);
        donationRepository.save(donation);
        return toDto(donation);
    }

    public DonationDto updateCondition(Long id, UpdateConditionRequest request) {
        Donation donation = donationRepository.findById(id).orElse(null);
        donation.setCondition(request.getCondition());
        donationRepository.save(donation);
        return toDto(donation);
    }

    public DonationDto toDto(Donation donation) {
        UserDto owner = userMapper.toDto(donation.getOwner());
        ItemDto item = itemServiceUtils.toDto(donation.getItem());
        return donationMapper.toDto(donation, owner, item);
    }


}
