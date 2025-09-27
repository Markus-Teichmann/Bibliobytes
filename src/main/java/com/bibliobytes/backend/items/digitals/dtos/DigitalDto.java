package com.bibliobytes.backend.items.digitals.dtos;

import com.bibliobytes.backend.items.items.dtos.ItemDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class DigitalDto extends ItemDto {
    private String runtime;
    private String label;
    private String production;
    private Set<ActorDto> actors;
    private Set<LanguageDto> languages;
    private Set<SubtitleDto> subtitles;
}
