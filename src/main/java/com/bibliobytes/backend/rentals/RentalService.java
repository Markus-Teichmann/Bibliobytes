package com.bibliobytes.backend.rentals;

import com.bibliobytes.backend.items.ItemService;
import com.bibliobytes.backend.items.items.dtos.ItemDto;
import com.bibliobytes.backend.items.items.entities.Item;
import com.bibliobytes.backend.rentals.dtos.RentalDto;
import com.bibliobytes.backend.rentals.entities.Rental;
import com.bibliobytes.backend.rentals.requests.UpdateRentalEndRequest;
import com.bibliobytes.backend.rentals.requests.UpdateRentalExternalRequest;
import com.bibliobytes.backend.rentals.requests.UpdateRentalStatusRequest;
import com.bibliobytes.backend.users.UserMapper;
import com.bibliobytes.backend.users.UserRepository;
import com.bibliobytes.backend.users.dtos.UserDto;
import com.bibliobytes.backend.users.entities.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RentalService {
    private final UserRepository userRepository;
    private RentalRepository rentalRepository;
    private RentalMapper rentalMapper;
    private UserMapper userMapper;

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
        Item item = rental.getDonation().getItem();
        ItemDto itemDto = itemService.toDto(item);
        UserDto user = userMapper.toDto(rental.getUser());
        UserDto external = userMapper.toDto(rental.getExternal());
        return rentalMapper.toDto(rental, itemDto, user, external);
    }
}
