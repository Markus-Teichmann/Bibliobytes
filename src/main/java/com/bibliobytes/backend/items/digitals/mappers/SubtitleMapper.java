package com.bibliobytes.backend.items.digitals.mappers;

import com.bibliobytes.backend.items.digitals.dtos.SubtitleDto;
import com.bibliobytes.backend.items.digitals.entities.Subtitle;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubtitleMapper {
    SubtitleDto toDto(Subtitle subtitle);
}
