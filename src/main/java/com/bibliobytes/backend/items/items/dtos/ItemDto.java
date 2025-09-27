package com.bibliobytes.backend.items.items.dtos;

import com.bibliobytes.backend.users.dtos.UserDto;
import lombok.Data;

import java.util.Set;

@Data
public class ItemDto {
    private Long id;
    private String title;
    private String place;
    private String topic;
    private String note;
    private Set<TagDto> tags;
    private Set<UserDto> owners;
    private int stock;
}
