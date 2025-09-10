package com.bibliobytes.backend.donations.dtos;

import com.bibliobytes.backend.donations.entities.DonationState;
import com.bibliobytes.backend.donations.entities.Condition;
import com.bibliobytes.backend.items.entities.Item;
import com.bibliobytes.backend.users.entities.User;
import lombok.Data;

import java.util.Date;

@Data
public class DonationDto {
    private User owner;
    private Item item;
    private Condition condition;
    private DonationState state;
    private Date date;
}
