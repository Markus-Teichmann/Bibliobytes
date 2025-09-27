package com.bibliobytes.backend.rentals;

import com.bibliobytes.backend.donations.entities.Donation;
import com.bibliobytes.backend.items.items.dtos.ItemDto;
import com.bibliobytes.backend.items.items.requests.RentItemRequest;
import com.bibliobytes.backend.rentals.dtos.RentalDto;
import com.bibliobytes.backend.rentals.entities.Rental;
import com.bibliobytes.backend.users.dtos.UserDto;
import com.bibliobytes.backend.users.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RentalMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    Rental toEntity(Donation donation, User user, RentItemRequest rentItemRequest, User external);
    @Mapping(target = "id", expression = "java(rental.getId())")
    RentalDto toDto(Rental rental, ItemDto itemDto, UserDto userDto, UserDto externalDto);
}
