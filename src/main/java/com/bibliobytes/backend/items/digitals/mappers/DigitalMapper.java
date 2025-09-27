package com.bibliobytes.backend.items.digitals.mappers;

import com.bibliobytes.backend.items.digitals.dtos.ActorDto;
import com.bibliobytes.backend.items.digitals.dtos.DigitalDto;
import com.bibliobytes.backend.items.digitals.dtos.LanguageDto;
import com.bibliobytes.backend.items.digitals.dtos.SubtitleDto;
import com.bibliobytes.backend.items.items.dtos.*;
import com.bibliobytes.backend.items.digitals.entities.Digital;
import com.bibliobytes.backend.items.items.requests.DonateNewItemRequest;
import com.bibliobytes.backend.users.dtos.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface DigitalMapper {
    @Mapping(target = "title", expression = "java(digital.getTitel())")
    DigitalDto toDto(Digital digital, Set<TagDto> tags, Set<ActorDto> actors, Set<LanguageDto> languages, Set<SubtitleDto> subtitles, Set<UserDto> owners, int stock);
    @Mapping(target = "tags", ignore = true)
    @Mapping(target="id", ignore = true)
    @Mapping(target="donations", ignore = true)
    @Mapping(target = "actors", ignore = true)
    @Mapping(target = "languages", ignore = true)
    @Mapping(target = "subtitles", ignore = true)
    @Mapping(target = "stock", ignore = true)
    @Mapping(target = "rentalCount", ignore = true)
    @Mapping(target = "state", ignore = true)
    Digital toEntity(DonateNewItemRequest request);
}
