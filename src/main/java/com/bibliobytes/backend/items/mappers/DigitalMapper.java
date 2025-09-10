package com.bibliobytes.backend.items.mappers;

import com.bibliobytes.backend.donations.entities.Donation;
import com.bibliobytes.backend.items.dtos.*;
import com.bibliobytes.backend.items.entities.Digital;
import com.bibliobytes.backend.users.dtos.UserDto;
import com.bibliobytes.backend.users.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface DigitalMapper {
    DigitalDto toDto(Digital digital, Set<TagDto> tags, Set<ActorDto> actors, Set<LanguageDto> languages, Set<SubtitleDto> subtitles, Set<UserDto> owners, int stock);
    @Mapping(target = "tags", ignore = true)
    @Mapping(target="id", ignore = true)
    @Mapping(target="donations", ignore = true)
    @Mapping(target = "actors", ignore = true)
    @Mapping(target = "languages", ignore = true)
    @Mapping(target = "subtitles", ignore = true)
    Digital toEntity(DonationRequest request);
}
