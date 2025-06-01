package com.bibliobytes.backend.services;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.SneakyThrows;

import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;

import java.time.Instant;
import java.util.Date;

@AllArgsConstructor
public class Jwe {
    private final JwtClaimsSet claims;
    private final RSAKey signingKey;
    private final RSAKey encryptionKey;
    private final JwtEncoder jwtEncoder;


    public boolean isExpired() {
        return claims.getExpiresAt().isBefore(Instant.now());
//        return claims.getIssueTime().before(new Date());
    }

    public String getSubject() {
        return claims.getSubject();
    }

    public <T> T get(String name, Class<T> requiredType) {
        Object value = claims.getClaim(name);
        if (requiredType.isInstance(value)) {
            return requiredType.cast(value);
        }
        return null;
    }

    @SneakyThrows
    public String toString() {
//        JWSHeader jwsHeader = new JWSHeader
//                .Builder(JWSAlgorithm.RS256)
//                .keyID(signingKey.getKeyID())
//                .build();
        JwsHeader jwsHeader = JwsHeader
                .with(SignatureAlgorithm.RS256)
                .keyId(signingKey.getKeyID())
                .build();

//        JWSObject jwsObject = new JWSObject(jwsHeader, claims.toPayload());
//        jwsObject.sign(new RSASSASigner(signingKey));

        String jws = jwtEncoder
                .encode(JwtEncoderParameters.from(jwsHeader, claims))
                .getTokenValue();

        JWEHeader jweHeader = new JWEHeader
                .Builder(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A128GCM)
                .contentType("JWT")
                .keyID(encryptionKey.getKeyID())
                .build();

//        JWEObject jweObject = new JWEObject(jweHeader, new Payload(jwsObject.serialize()));
        JWEObject jweObject = new JWEObject(jweHeader, new Payload(jws));
        jweObject.encrypt(new RSAEncrypter(encryptionKey.toRSAPublicKey()));

        return jweObject.serialize();
    }
}
