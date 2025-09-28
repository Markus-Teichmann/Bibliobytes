package com.bibliobytes.backend.auth.services.jwe;

import com.bibliobytes.backend.auth.config.JweConfig;
import com.bibliobytes.backend.auth.services.mail.MailService;
import com.bibliobytes.backend.auth.dtos.AccessTokenDto;
import com.bibliobytes.backend.auth.dtos.RefreshTokenDto;
import com.bibliobytes.backend.users.requests.RegisterUserRequest;
import com.bibliobytes.backend.users.requests.UpdateEmailRequest;
import com.bibliobytes.backend.users.requests.UpdatePasswordRequest;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class JweService {
    private MailService mailService;
    private JweConfig config;

    public Jwe generateRefreshToken(RefreshTokenDto dto) throws Exception {
        return generateToken(
                dto.getId().toString(),
                Map.of(
                        "dto", dto,
                        "dtoClassName", dto.getClass().getName()
                ),
                config.getRefreshTokenExpiration()
        );
    }

    public Jwe generateAccessToken(AccessTokenDto dto) throws Exception {
        return generateToken(
                dto.getId().toString(),
                Map.of(
                        "dto", dto,
                        "dtoClassName", dto.getClass().getName()
                ),
                config.getAccessTokenExpiration()
        );
    }

    public Jwe generateRegisterUserToken(RegisterUserRequest dto) throws Exception {
        System.out.println("Hello from JweService");
        String code = mailService.sendCodeTo(dto.getEmail());
        System.out.println("Code: " + code);
        return generateToken(
                dto.getEmail(),
                Map.of(
                        "code", code,
                        "dto", dto,
                        "dtoClassName", dto.getClass().getName()
                ),
                config.getRegisterUserTokenExpiration()
        );

    }

    public Jwe generateUpdateEmailToken(UUID id, UpdateEmailRequest request) throws Exception {
        String code = mailService.sendCodeTo(request.getOldEmail());
        code += mailService.sendCodeTo(request.getNewEmail());
        return generateToken(
                id.toString(),
                Map.of(
                        "code", code,
                        "dto", request,
                        "dtoClassName", request.getClass().getName()
                ),
                config.getUpdateUserCredentialsTokenExpiration()
        );
    }

    public Jwe generateUpdatePasswordToken(UUID id, UpdatePasswordRequest request, String email) throws Exception {
        String code = mailService.sendCodeTo(email);
        return generateToken(
                id.toString(),
                Map.of(
                        "code", code,
                        "dto", request,
                        "dtoClassName", request.getClass().getName()
                ),
                config.getUpdateUserCredentialsTokenExpiration()
        );
    }

    private Jwe generateToken(String subject, Map<String, Object> claims, long expiration) throws Exception {
        var claimSetBuilder = new JWTClaimsSet.Builder()
            .subject(subject)
            .issuer(config.getIssuer())
            .issueTime(new Date())
            .expirationTime(new Date(System.currentTimeMillis() + 1000 * expiration));
        for (Map.Entry<String, Object> claimEntry : claims.entrySet()) {
            if (claimEntry.getValue() instanceof String value) {
                claimSetBuilder.claim(claimEntry.getKey(), value);
            } else {
                claimSetBuilder.claim(claimEntry.getKey(), serialise(claimEntry.getValue()));
            }
        }
        return new Jwe(
                claimSetBuilder.build(),
                config.signingKey(),
                config.encryptionKey()
        );
    }

    private String serialise(Object dto) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(dto);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
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
}