package com.bibliobytes.backend.items.dtos;

import com.bibliobytes.backend.users.entities.User;
import lombok.Data;

import java.util.List;

@Data
public class BookDto {
    private String title;
    private String place;
    private String topic;
    private List<User> owners;
    private int stock;
    private String note;
    private String publisher;
    public String isbn;
    public String author;
}
