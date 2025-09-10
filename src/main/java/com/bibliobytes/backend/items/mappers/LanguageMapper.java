package com.bibliobytes.backend.items.mappers;

import com.bibliobytes.backend.items.dtos.LanguageDto;
import com.bibliobytes.backend.items.entities.Language;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LanguageMapper {
    LanguageDto toDto(Language language);
}
