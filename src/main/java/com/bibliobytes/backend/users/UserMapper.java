package com.bibliobytes.backend.users;

import com.bibliobytes.backend.users.requests.RegisterUserRequest;
import com.bibliobytes.backend.users.dtos.UserDto;
import com.bibliobytes.backend.users.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "donations", ignore = true)
    @Mapping(target = "rentals", ignore = true)
    User toEntity(RegisterUserRequest request);
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "donations", ignore = true)
    @Mapping(target = "rentals", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toExternal(String email, String firstName, String lastName);
}
