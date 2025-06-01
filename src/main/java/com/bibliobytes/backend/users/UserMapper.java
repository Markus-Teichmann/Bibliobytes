package com.bibliobytes.backend.users;

import com.bibliobytes.backend.services.Jwe;
import com.bibliobytes.backend.users.dtos.RegisterUserRequest;
import com.bibliobytes.backend.users.dtos.UserDto;
import com.bibliobytes.backend.users.entities.User;
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
    User toEntity(Jwe registerToken);

//    @Mapping(target = "email", expression = "java(java.util.Base64.getEncoder().encodeToString(request.getEmail().getBytes()))")
//    @Mapping(target = "firstName", expression = "java(java.util.Base64.getEncoder().encodeToString(request.getFirstName().getBytes()))")
//    @Mapping(target = "lastName", expression = "java(java.util.Base64.getEncoder().encodeToString(request.getLastName().getBytes()))")
//    @Mapping(target = "password", expression = "java(java.util.Base64.getEncoder().encodeToString(request.getPassword().getBytes()))")
//    RegisterUserRequest encodeBase64(RegisterUserRequest request);
//
//    @Mapping(target = "email", expression = "java(new String(java.util.Base64.getDecoder().decode(token.getSubject())))")
//    @Mapping(target = "firstName", expression = "java(new String(java.util.Base64.getDecoder().decode(token.get(\"firstName\", String.class))))")
//    @Mapping(target = "lastName", expression = "java(new String(java.util.Base64.getDecoder().decode(token.get(\"lastName\", String.class))))")
//    @Mapping(target = "password", expression = "java(new String(java.util.Base64.getDecoder().decode(token.get(\"password\", String.class))))")
//    RegisterUserRequest decodeBase64(Jwt token);
}
