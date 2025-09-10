package com.bibliobytes.backend.items.dtos;

import com.bibliobytes.backend.users.dtos.UserDto;
import com.bibliobytes.backend.users.entities.User;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class BookDto {
    private String titel;
    private String place;
    private String topic;
    private String note;
    private Set<TagDto> tags;
    public String author;
    private String publisher;
    public String isbn;
    private Set<UserDto> owners;
    private int stock;
}
