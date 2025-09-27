package com.bibliobytes.backend.items.books.dtos;

import com.bibliobytes.backend.items.items.dtos.ItemDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookDto extends ItemDto {
    public String author;
    private String publisher;
    public String isbn;
}
