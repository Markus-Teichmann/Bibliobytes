package com.bibliobytes.backend.rentals.dtos;

import com.bibliobytes.backend.items.items.dtos.ItemDto;
import com.bibliobytes.backend.users.dtos.UserDto;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RentalDto {
    private Long id;
    private ItemDto itemDto;
    private UserDto userDto;
    private LocalDate startDate;
    private LocalDate endDate;
    private UserDto externalDto;
}
