package com.bibliobytes.backend.auth;

import com.bibliobytes.backend.auth.dtos.AccessTokenDto;
import com.bibliobytes.backend.auth.dtos.RefreshTokenDto;
import com.bibliobytes.backend.users.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TokenMapper {
    AccessTokenDto toAccessTokenDto(User user);
    RefreshTokenDto toRefreshTokenDto(User user);
}
