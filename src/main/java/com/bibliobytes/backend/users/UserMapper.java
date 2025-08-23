package com.bibliobytes.backend.users;

import com.bibliobytes.backend.auth.services.Jwe;
import com.bibliobytes.backend.auth.dtos.AccessTokenDto;
import com.bibliobytes.backend.auth.dtos.RefreshTokenDto;
import com.bibliobytes.backend.users.dtos.RegisterUserRequest;
import com.bibliobytes.backend.users.dtos.UserDto;
import com.bibliobytes.backend.users.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    //AccessTokenDto toAccessTokenDto(User user);
    //RefreshTokenDto toRefreshTokenDto(User user);

    User toEntity(RegisterUserRequest request);

//    @Mapping(target = "email", expression = "java(registerToken.getSubject())")
//    @Mapping(target = "firstName", expression = "java(registerToken.get(\"firstName\", String.class))")
//    @Mapping(target = "lastName", expression = "java(registerToken.get(\"lastName\", String.class))")
//    @Mapping(target = "password", expression = "java(registerToken.get(\"password\", String.class))")
//    User toEntity(Jwe registerToken);
}
