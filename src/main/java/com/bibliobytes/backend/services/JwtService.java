package com.bibliobytes.backend.services;

import com.bibliobytes.backend.config.JwtConfig;
import com.bibliobytes.backend.dtos.RegisterUserRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class JwtService {
    private JwtConfig config;

    public Jwt generateRegisterRequestToken(RegisterUserRequest request, String verificationCode) {
        var claims = Jwts.claims()
                .subject(request.getEmail())
                .add("firstName", request.getFirstName())
                .add("lastName", request.getLastName())
                .add("password", request.getPassword())
                .add("verificationCode", verificationCode)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * config.getRegisterRequestTokenExpiration()))
                .build();
        return new Jwt(claims, config.getSigningKey());
    }

    public Jwt parse(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(config.getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return new Jwt(claims, config.getSigningKey());
        } catch (JwtException e) {
            return null;
        }
    }
}
