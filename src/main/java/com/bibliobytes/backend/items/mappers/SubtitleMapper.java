package com.bibliobytes.backend.items.mappers;

import com.bibliobytes.backend.items.dtos.SubtitleDto;
import com.bibliobytes.backend.items.entities.Subtitle;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubtitleMapper {
    SubtitleDto toDto(Subtitle subtitle);
}
