package com.bibliobytes.backend.items.digitals.mappers;

import com.bibliobytes.backend.items.digitals.dtos.LanguageDto;
import com.bibliobytes.backend.items.digitals.entities.Language;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LanguageMapper {
    LanguageDto toDto(Language language);
}
