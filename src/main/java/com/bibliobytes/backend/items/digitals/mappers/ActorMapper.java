package com.bibliobytes.backend.items.digitals.mappers;

import com.bibliobytes.backend.items.digitals.dtos.ActorDto;
import com.bibliobytes.backend.items.digitals.entities.Actor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActorMapper {
    ActorDto toDto(Actor actor);
}
