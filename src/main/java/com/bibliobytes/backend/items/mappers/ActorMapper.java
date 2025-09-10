package com.bibliobytes.backend.items.mappers;

import com.bibliobytes.backend.items.dtos.ActorDto;
import com.bibliobytes.backend.items.entities.Actor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActorMapper {
    ActorDto toDto(Actor actor);
}
