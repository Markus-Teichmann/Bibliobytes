package com.bibliobytes.backend.auth;

import com.bibliobytes.backend.users.dtos.RegisterUserRequest;
import com.bibliobytes.backend.users.dtos.UpdateCredentialsDto;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface TokenMapper {
    @Valid
    @Mapping(target = "email", expression = "java(subject)")
    @Mapping(target = "id", expression = "java(java.util.UUID.fromString(String.valueOf(claims.get(\"id\"))))")
    @Mapping(target = "password", expression = "java(String.valueOf(claims.get(\"password\")))")
    UpdateCredentialsDto toUpdateCredentialsDto(String subject, Map<String, Object> claims);

    @Valid
    @Mapping(target = "email", expression = "java(subject)")
    @Mapping(target = "firstName", expression = "java(String.valueOf(claims.get(\"firstName\")))")
    @Mapping(target = "lastName", expression = "java(String.valueOf(claims.get(\"lastName\")))")
    @Mapping(target = "password", expression = "java(String.valueOf(claims.get(\"password\")))")
    RegisterUserRequest toRegisterUserRequest(String subject, Map<String, Object> claims);

    enum Switch {
        UpdateCredentialsDto {
            @Override
            public UpdateCredentialsDto create(String subject, Map<String, Object> claims, TokenMapper tokenMapper) {
                return tokenMapper.toUpdateCredentialsDto(subject, claims);
            }
        },
        RegisterUserRequest {
            @Override
            public RegisterUserRequest create(String subject, Map<String, Object> claims, TokenMapper tokenMapper) {
                return tokenMapper.toRegisterUserRequest(subject, claims);
            }
        };
        public abstract Object create(String subject, Map<String, Object> claims, TokenMapper tokenMapper);
    }
}
