package com.bibliobytes.backend.items.dtos;

import com.bibliobytes.backend.items.entities.Actor;
import com.bibliobytes.backend.items.entities.Language;
import com.bibliobytes.backend.items.entities.Subtitle;
import com.bibliobytes.backend.users.dtos.UserDto;
import com.bibliobytes.backend.users.entities.User;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class DigitalDto {
    private String titel;
    private String place;
    private String topic;
    private String note;
    private Set<TagDto> tags;
    private String runtime;
    private String label;
    private String production;
    private Set<ActorDto> actors;
    private Set<LanguageDto> languages;
    private Set<SubtitleDto> subtitles;
    private Set<UserDto> owners;
    private int stock;
}
