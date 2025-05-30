package com.bibliobytes.backend.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

@AllArgsConstructor
public class Jwt {
    private final Claims claims;
    private final SecretKey key;

    public boolean isExpired() {
        return claims.getExpiration().before(new Date());
    }

    public String getSubject() {
        return claims.getSubject();
    }

    public <T> T get(String name, Class<T> clazz) {
        return claims.get(name, clazz);
    }

    public String toString() {
        return Jwts.builder()
                .claims(claims)
                .signWith(key)
                .compact();
    }

}
