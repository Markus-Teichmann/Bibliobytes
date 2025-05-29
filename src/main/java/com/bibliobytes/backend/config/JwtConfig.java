package com.bibliobytes.backend.config;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
@ConfigurationProperties(prefix = "spring.jwt")
@Getter
@Setter
public class JwtConfig {
    private String signingKey;
    private long registerRequestTokenExpiration;
    private long refreshTokenExpiration;
    private long accessTokenExpiration;

    public SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(signingKey.getBytes());
    }
}
