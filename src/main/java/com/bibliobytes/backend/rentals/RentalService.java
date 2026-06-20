package com.bibliobytes.backend.rentals;

import com.bibliobytes.backend.donations.DonationRepository;
import com.bibliobytes.backend.donations.DonationService;
import com.bibliobytes.backend.donations.entities.Donation;
import com.bibliobytes.backend.items.ItemService;
import com.bibliobytes.backend.items.items.dtos.ItemDto;
import com.bibliobytes.backend.items.items.entities.Item;
import com.bibliobytes.backend.items.items.repositorys.ItemRepository;
import com.bibliobytes.backend.rentals.dtos.RentalDto;
import com.bibliobytes.backend.rentals.entities.Rental;
import com.bibliobytes.backend.rentals.requests.CreateRentalRequest;
import com.bibliobytes.backend.rentals.requests.UpdateRentalEndRequest;
import com.bibliobytes.backend.rentals.requests.UpdateRentalExternalRequest;
import com.bibliobytes.backend.rentals.requests.UpdateRentalStatusRequest;
import com.bibliobytes.backend.users.UserMapper;
import com.bibliobytes.backend.users.UserRepository;
import com.bibliobytes.backend.users.UserService;
import com.bibliobytes.backend.users.dtos.UserDto;
import com.bibliobytes.backend.users.entities.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class RentalService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final DonationRepository donationRepository;
    private final DonationService donationService;
    private final ItemRepository itemRepository;
    private RentalRepository rentalRepository;
    private RentalMapper rentalMapper;
    private UserMapper userMapper;

    public RentalDto createRental(CreateRentalRequest request) {
        User external = null;
        if (request.external()) {
            external = userRepository.findByEmail(request.getExternalEmail()).orElse(null);
            if (external == null) {
                external = userService.registerExternal(
                        request.getExternalEmail(),
                        request.getExternalFirstName(),
                        request.getExternalLastName()
                );
            }
        }
        UUID myId = userService.getMyId();
        User me = userRepository.findById(myId).orElse(null);
        Donation donation = donationRepository.findById(request.getDonationId()).orElse(null);
        Rental rental = rentalMapper.toEntity(donation, me, request.getStartDate(), request.getEndDate(), external);
        rentalRepository.save(rental);
        if (donation != null) {
            donation.getItem().decrementStock();
            itemRepository.save(donation.getItem());
        }
        return rentalMapper.toDto(
                rental,
                donationService.toDto(donation),
                userMapper.toDto(me),
                userMapper.toDto(external)
        );
    }

    public RentalDto getRental(long id, ItemService itemService) {
        Rental rental = rentalRepository.findById(id).orElse(null);
        return toDto(rental, itemService);
    }

    public RentalDto updateStatus(long id, UpdateRentalStatusRequest request, ItemService itemService) {
        Rental rental = rentalRepository.findById(id).orElse(null);
        rental.setStatus(request.getState());
        rentalRepository.save(rental);
        return toDto(rental, itemService);
    }

    public RentalDto updateExternal(long id, UpdateRentalExternalRequest request, ItemService itemService) {
        Rental rental = rentalRepository.findById(id).orElse(null);
        User external = userRepository.findById(request.getUserId()).orElse(null);
        if (external == null) {
            external = userMapper.toExternal(request.getEmail(),request.getFirstName(), request.getLastName());
            userRepository.save(external);
        }
        rental.setExternal(external);
        rentalRepository.save(rental);
        return toDto(rental, itemService);
    }

    public RentalDto updateEnd(long id, UpdateRentalEndRequest request, ItemService itemService) {
        Rental rental = rentalRepository.findById(id).orElse(null);
        if (rental.getStartDate().isAfter(request.getRentalEndDate())) {
            return null;
        }
        rental.setEndDate(request.getRentalEndDate());
        rentalRepository.save(rental);
        return toDto(rental, itemService);
    }

    public RentalDto toDto(Rental rental, ItemService itemService) {
        Long itemId = rental.getDonation().getItem().getId();
        ItemDto itemDto = itemService.toDto(itemId);
        UserDto user = userMapper.toDto(rental.getUser());
        UserDto external = userMapper.toDto(rental.getExternal());
        return rentalMapper.toDto(rental, itemDto, user, external);
    }
}
