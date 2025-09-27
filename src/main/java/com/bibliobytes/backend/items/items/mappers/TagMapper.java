package com.bibliobytes.backend.items.items.mappers;

import com.bibliobytes.backend.items.items.dtos.TagDto;
import com.bibliobytes.backend.items.items.entities.Tag;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TagMapper {
    TagDto toDto(Tag tag);
}
