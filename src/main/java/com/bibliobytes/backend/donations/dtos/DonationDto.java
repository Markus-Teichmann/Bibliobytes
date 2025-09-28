package com.bibliobytes.backend.donations.dtos;

import com.bibliobytes.backend.donations.entities.DonationState;
import com.bibliobytes.backend.donations.entities.Condition;
import com.bibliobytes.backend.items.items.dtos.ItemDto;
import com.bibliobytes.backend.users.dtos.UserDto;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class DonationDto {
    private Long id;
    private UserDto owner;
    private ItemDto item;
    private Condition condition;
    private DonationState state;
    private LocalDate date;
}
