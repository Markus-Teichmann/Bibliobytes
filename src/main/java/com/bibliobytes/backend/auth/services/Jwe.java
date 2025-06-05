package com.bibliobytes.backend.auth.services;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;

import lombok.AllArgsConstructor;
import java.util.Date;

@AllArgsConstructor
public class Jwe {
    private final JWTClaimsSet claims;
    private final RSAKey signingKey;
    private final RSAKey encryptionKey;

    public boolean isExpired() {
        return claims.getExpirationTime().before(new Date());
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
        JWSHeader jwsHeader = new JWSHeader
                .Builder(JWSAlgorithm.RS256)
                .keyID(signingKey.getKeyID())
                .build();

        SignedJWT signedJWT = new SignedJWT(jwsHeader, claims);
        signedJWT.sign(new RSASSASigner(signingKey));

        JWEHeader jweHeader = new JWEHeader
                .Builder(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A128GCM)
                .contentType("JWT")
                .keyID(encryptionKey.getKeyID())
                .build();

        JWEObject jweObject = new JWEObject(jweHeader, new Payload(signedJWT));
        jweObject.encrypt(new RSAEncrypter(encryptionKey.toRSAPublicKey()));

        return jweObject.serialize();
    }
}
