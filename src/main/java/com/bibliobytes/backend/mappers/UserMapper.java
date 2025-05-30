package com.bibliobytes.backend.mappers;

import com.bibliobytes.backend.dtos.*;
import com.bibliobytes.backend.entities.User;
import com.bibliobytes.backend.services.Jwt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    User toEntity(RegisterUserRequest request);

    @Mapping(target = "email", expression = "java(registerToken.getSubject())")
    @Mapping(target = "firstName", expression = "java(registerToken.get(\"firstName\", String.class))")
    @Mapping(target = "lastName", expression = "java(registerToken.get(\"lastName\", String.class))")
    @Mapping(target = "password", expression = "java(registerToken.get(\"password\", String.class))")
    //@Mapping(target = "role", expression = "java(com.bibliobytes.backend.entities.Role.valueOf(registerToken.get(\"role\", String.class)))")
    User toEntity(Jwt registerToken);

}
