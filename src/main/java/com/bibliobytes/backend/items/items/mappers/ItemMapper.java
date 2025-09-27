package com.bibliobytes.backend.items.items.mappers;

import com.bibliobytes.backend.items.items.dtos.ItemDto;
import com.bibliobytes.backend.items.items.dtos.TagDto;
import com.bibliobytes.backend.items.items.entities.Item;
import com.bibliobytes.backend.users.dtos.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    @Mapping(target = "title", expression = "java(item.getTitel())")
    ItemDto toDto(Item item, Set<TagDto> tags, Set<UserDto> owners, int stock);
}
