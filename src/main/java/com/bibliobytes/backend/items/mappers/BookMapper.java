package com.bibliobytes.backend.items.mappers;

import com.bibliobytes.backend.donations.entities.Donation;
import com.bibliobytes.backend.items.dtos.BookDto;
import com.bibliobytes.backend.items.dtos.DonationRequest;
import com.bibliobytes.backend.items.dtos.TagDto;
import com.bibliobytes.backend.items.entities.Book;
import com.bibliobytes.backend.users.dtos.UserDto;
import com.bibliobytes.backend.users.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookDto toDto(Book book, Set<TagDto> tags, Set<UserDto> owners, int stock);
    @Mapping(target="tags", ignore = true)
    @Mapping(target="id", ignore = true)
    @Mapping(target="donations", ignore = true)
    Book toEntity(DonationRequest request);
}
