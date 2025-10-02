package com.bibliobytes.backend.auth.services.jwe;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;

import lombok.AllArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

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

    public String getCode() {
        return (String) claims.getClaim("code");
    }

    public <T> T toDto() throws IOException {
        Class<T> type = (Class<T>) getDtoClass();
        Object deserializedDto = deserialize();
        if (!type.isInstance(deserializedDto)) {
            return null;
        }
        return type.cast(deserializedDto);
    }

    private Class<?> getDtoClass() throws IOException {
        String className = (String) claims.getClaim("dtoClassName");
        Class<?> type = null;
        try {
            type = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IOException("When deserialzing Token - No Class with Name: " + className);
        }
        return type;
    }

    private Object deserialize() {
        Object serializedDto = claims.getClaim("dto");
        if (!(serializedDto instanceof String)) {
            return null;
        }
        byte[] data = Base64.getDecoder().decode(serializedDto.toString());
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            Object obj = ois.readObject();
            ois.close();
            return obj;
        } catch (Exception e) {
            System.out.println("Error while deserializing token");
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
