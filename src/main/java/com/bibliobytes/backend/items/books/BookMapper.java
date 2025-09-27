package com.bibliobytes.backend.items.books;

import com.bibliobytes.backend.items.books.dtos.BookDto;
import com.bibliobytes.backend.items.items.requests.DonateNewItemRequest;
import com.bibliobytes.backend.items.items.dtos.TagDto;
import com.bibliobytes.backend.users.dtos.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface BookMapper {
    @Mapping(target = "title", expression = "java(book.getTitel())")
    BookDto toDto(Book book, Set<TagDto> tags, Set<UserDto> owners, int stock);
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "donations", ignore = true)
    @Mapping(target = "stock", ignore = true)
    @Mapping(target = "rentalCount", ignore = true)
    @Mapping(target = "state", ignore = true)
    Book toEntity(DonateNewItemRequest request);
}
