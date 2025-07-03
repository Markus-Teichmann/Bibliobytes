package com.bibliobytes.backend.auth.services;

import com.bibliobytes.backend.auth.TokenMapper;
import com.bibliobytes.backend.auth.config.JweConfig;
import com.bibliobytes.backend.email.MailService;
import com.bibliobytes.backend.users.entities.User;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AllArgsConstructor;
import org.slf4j.event.KeyValuePair;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class JweService {
    private TokenMapper tokenMapper;
    private MailService mailService;
    private JweConfig config;

    public Jwe generateRefreshToken(User user) throws Exception {
        return generateToken(
                user.getId().toString(),
                Map.of(
                        "email", user.getEmail(),
                        "firstName", user.getFirstName(),
                        "lastName", user.getLastName()
                ),
                config.getRefreshTokenExpiration()
        );
    }

    public Jwe generateAccessToken(User user) throws Exception {
        return generateToken(
                user.getId().toString(),
                Map.of(
                        "role", user.getRole()
                ),
                config.getAccessTokenExpiration()
        );

    }

    public Jwe generateConfirmableToken(String email, Map<String, Object> claims) throws Exception {
        Map<String, Object> mutableClaims = new HashMap<>(claims);
        String code = mailService.sendCodeTo(email);
        mutableClaims.put("code", code);
        return generateToken(
                email,
                mutableClaims,
                config.getConfirmableTokenExpiration()
        );
    }

    private Jwe generateToken(String subject, Map<String, Object> claims, long expiration) throws Exception {
        var claimSetBuilder = new JWTClaimsSet.Builder()
            .subject(subject)
            .issuer(config.getIssuer())
            .issueTime(new Date())
            .expirationTime(new Date(System.currentTimeMillis() + 1000 * expiration));
        for (Map.Entry<String, Object> claimEntry : claims.entrySet()) {
            claimSetBuilder.claim(claimEntry.getKey(), claimEntry.getValue());
        }
        return new Jwe(
                claimSetBuilder.build(),
                config.signingKey(),
                config.encryptionKey()
        );
    }

    public Jwe parse(String token) {
        try {
            JWEObject jwe = JWEObject.parse(token);
            jwe.decrypt(new RSADecrypter(config.encryptionKey().toRSAPrivateKey()));
            SignedJWT signedJWT = jwe.getPayload().toSignedJWT();
            if(signedJWT.verify(new RSASSAVerifier(config.signingKey()))) {
                return new Jwe(
                        signedJWT.getJWTClaimsSet(),
                        config.signingKey(),
                        config.encryptionKey()
                );
            }
            return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Object confirmedData(String token, String code) {
        Jwe jwe = parse(token);
        if (jwe == null || jwe.isExpired()) {
            return Map.of("message", "Token expired");
        }
        if (!code.matches(jwe.get("code", String.class))) {
            return Map.of("message", "Invalid code");
        }
        String type = jwe.get("type", String.class);
        return TokenMapper.Switch.valueOf(type).create(jwe.getSubject(), jwe.getClaims(), tokenMapper);
    }
}
