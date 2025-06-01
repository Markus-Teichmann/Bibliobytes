package com.bibliobytes.backend.services;

import com.bibliobytes.backend.config.JweConfig;
import com.bibliobytes.backend.users.dtos.RegisterUserRequest;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@AllArgsConstructor
public class JweService {
    private JweConfig config;

    public Jwe generateRegisterRequestToken(RegisterUserRequest request, String verificationCode) throws Exception {
        var claims = JwtClaimsSet.builder()
                .subject(request.getEmail())
                .claim("firstName", request.getFirstName())
                .claim("lastName", request.getLastName())
                .claim("password", request.getPassword())
                .claim("code", verificationCode)
                .issuer(config.getIssuer())
                .issuedAt(Instant.now())
                .expiresAt(Instant.ofEpochMilli(System.currentTimeMillis() + 1000 * config.getRegisterRequestTokenExpiration()))
                .build();
        return new Jwe(
                claims,
                config.signingKey(),
                config.encryptionKey(),
                config.jwtEncoder()
                );
    }

    public Jwe parse(String token) {
        try {
            JWEObject jwe = JWEObject.parse(token);
            jwe.decrypt(new RSADecrypter(config.encryptionKey().toRSAPrivateKey()));

            JWSObject jwsObject = jwe.getPayload().toJWSObject();
            if (jwsObject.verify(new RSASSAVerifier(config.signingKey()))) {
                Payload payload = jwsObject.getPayload();
                JwtClaimsSet.Builder jwtSetBuilder = JwtClaimsSet.builder();
                for (Map.Entry<String, Object> entry: payload.toJSONObject().entrySet()) {
                    jwtSetBuilder.claim(entry.getKey(), entry.getValue());
                }
                return new Jwe(
                        jwtSetBuilder.build(),
                        config.signingKey(),
                        config.encryptionKey(),
                        config.jwtEncoder()
                );
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
