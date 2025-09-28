package com.bibliobytes.backend.donations;

import com.bibliobytes.backend.donations.dtos.*;
import com.bibliobytes.backend.donations.entities.Donation;
import com.bibliobytes.backend.donations.entities.DonationState;
import com.bibliobytes.backend.donations.requests.*;
import com.bibliobytes.backend.items.ItemService;
import com.bibliobytes.backend.items.ItemServiceDispatcher;
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
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private DonationRepository donationRepository;
    private DonationMapper donationMapper;
    private UserMapper userMapper;

    public Set<DonationDto> getAllDonations(UUID ownerId, ItemService itemService) {
        return donationRepository.findAllByOwnerId(ownerId).stream()
                .map(donation -> toDto(donation, itemService))
                .collect(Collectors.toSet());
    }

    public DonationDto getDonationBy(Long id, ItemService itemService) {
        return donationRepository.findById(id).stream()
                .map(donation -> toDto(donation, itemService)).findFirst()
                .orElse(null);
    }

    public DonationDto withdrawDonation(UUID userId, WithdrawDonationRequest request, ItemService itemService) {
        Set<Long> donationIds = donationRepository.findAllDonationIdsByOwnerId(userId);
        if (!donationIds.contains(request.getDonationId())) {
            return null;
        }
        Donation donation = donationRepository.findById(request.getDonationId()).orElse(null);
        if (donation != null) {
            donation.setStatus(DonationState.WITHDRAWN);
            donationRepository.save(donation);
        }
        return toDto(donation, itemService);
    }

    public DonationDto updateDonationStatus(Long id, UpdateDonationStatusRequest request, ItemService itemService) {
        Donation donation = donationRepository.findById(id).orElse(null);
        donation.setStatus(request.getState());
        donationRepository.save(donation);
        return toDto(donation, itemService);
    }

    public DonationDto updateItem(Long id, UpdateItemRequest request, ItemService itemService) {
        Donation donation = donationRepository.findById(id).orElse(null);
        Item item = itemRepository.findById(request.getItemId()).orElse(null);
        donation.setItem(item);
        donationRepository.save(donation);
        return toDto(donation, itemService);
    }

    public DonationDto updateOwner(Long id, UpdateOwnerRequest request, ItemService itemService) {
        Donation donation = donationRepository.findById(id).orElse(null);
        User owner = userRepository.findById(request.getOwnerId()).orElse(null);
        donation.setOwner(owner);
        donationRepository.save(donation);
        return toDto(donation, itemService);
    }

    public DonationDto updateCondition(Long id, UpdateConditionRequest request, ItemService itemService) {
        Donation donation = donationRepository.findById(id).orElse(null);
        donation.setCondition(request.getCondition());
        donationRepository.save(donation);
        return toDto(donation, itemService);
    }

    public DonationDto toDto(Donation donation, ItemService itemService) {
        UserDto owner = userMapper.toDto(donation.getOwner());
        ItemDto item = itemService.toDto(donation.getItem());
        return donationMapper.toDto(donation, owner, item);
    }


}
