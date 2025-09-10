package com.bibliobytes.backend.items.mappers;

import com.bibliobytes.backend.items.dtos.TagDto;
import com.bibliobytes.backend.items.entities.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TagMapper {
    TagDto toDto(Tag tag);
}
