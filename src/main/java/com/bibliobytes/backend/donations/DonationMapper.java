package com.bibliobytes.backend.donations;

import com.bibliobytes.backend.donations.dtos.DonationDto;
import com.bibliobytes.backend.donations.entities.Donation;
import com.bibliobytes.backend.items.items.dtos.ItemDto;
import com.bibliobytes.backend.users.dtos.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DonationMapper {
    //Donation toEntity(DonationDto dto);
    @Mapping(target = "state", expression = "java(donation.getStatus())")
    @Mapping(target = "id", expression = "java(donation.getId())")
    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "item", source = "item")
    DonationDto toDto(Donation donation, UserDto owner, ItemDto item);
}
