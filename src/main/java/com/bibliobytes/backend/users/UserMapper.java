package com.bibliobytes.backend.users;

import com.bibliobytes.backend.users.dtos.RegisterUserRequest;
import com.bibliobytes.backend.users.dtos.UserDto;
import com.bibliobytes.backend.users.entities.User;
import org.mapstruct.Mapper;

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
