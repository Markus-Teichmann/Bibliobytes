package com.bibliobytes.backend.mappers;

import com.bibliobytes.backend.dtos.*;
import com.bibliobytes.backend.entities.External;
import com.bibliobytes.backend.entities.Internal;
import com.bibliobytes.backend.entities.Role;
import com.bibliobytes.backend.services.Jwt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "email", expression = "java(user.getExternal().getEmail())")
    @Mapping(target = "firstName", expression = "java(user.getExternal().getFirstName())")
    @Mapping(target = "lastName", expression = "java(user.getExternal().getLastName())")
    UserDto toUserDto(Internal user);
    UserDto toUserDto(External user);

    @Mapping(target = "email", expression = "java(registerToken.get(\"email\", String.class))")
    @Mapping(target = "firstName", expression = "java(registerToken.get(\"firstName\", String.class))")
    @Mapping(target = "lastName", expression = "java(registerToken.get(\"lastName\", String.class))")
    @Mapping(target = "password", expression = "java(registerToken.get(\"password\", String.class))")
    RegisterUserRequest toUserRequest(Jwt registerToken);

    External toExternal(RegisterUserRequest request);
    Internal toInternal(RegisterUserRequest request, Role role);

    void updateExternal(RegisterUserRequest request, @MappingTarget External external);
    void updateInternal(RegisterUserRequest request, @MappingTarget Internal internal);
    void updateInternal(Role role, @MappingTarget Internal internal);
}
