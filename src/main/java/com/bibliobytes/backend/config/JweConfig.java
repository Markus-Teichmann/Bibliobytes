package com.bibliobytes.backend.config;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWEDecryptionKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "spring.jwe")
@Getter
@Setter
public class JweConfig {

    @Data
    public static class Pair {
        private String keyId;
        private String publicKey;
        private String privateKey;
    }
    private String issuer;

    private long registerRequestTokenExpiration;
    private long refreshTokenExpiration;
    private long accessTokenExpiration;

    private Pair signing;
    private Pair encryption;

    public RSAKey signingKey() throws Exception {
        return buildRsaKey(
                signing.getPublicKey(),
                signing.getPrivateKey(),
                signing.getKeyId(),
                true
        );
    }

    public RSAKey encryptionKey() throws Exception {
        return buildRsaKey(
                encryption.getPublicKey(),
                encryption.getPrivateKey(),
                encryption.getKeyId(),
                false
        );
    }

    private RSAKey buildRsaKey(String pubKey, String priKey, String keyId, boolean signing) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] decodedPublicKey = Base64.getDecoder().decode(pubKey);
        byte[] decodedPrivateKey = Base64.getDecoder().decode(priKey);

        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(decodedPublicKey);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(decodedPrivateKey);

        RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
        RSAPrivateKey privateKey =  (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);

        RSAKey.Builder builder = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(keyId);

        if (signing) {
            builder.algorithm(JWSAlgorithm.RS256).keyUse(KeyUse.SIGNATURE);
        } else {
            builder.algorithm(JWEAlgorithm.RSA_OAEP_256).keyUse(KeyUse.ENCRYPTION);
        }

        return builder.build();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() throws Exception {
        JWKSet jwkSet = new JWKSet(List.of(
                signingKey(),
                encryptionKey()
        ));
        return (jwkSelector, context) -> jwkSelector.select(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        DefaultJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        JWEDecryptionKeySelector<SecurityContext> jweKeySelector =
                new JWEDecryptionKeySelector<>(
                        JWEAlgorithm.RSA_OAEP_256,
                        EncryptionMethod.A128GCM,
                        jwkSource
                );
        jwtProcessor.setJWEKeySelector(jweKeySelector);

        JWSVerificationKeySelector<SecurityContext> jwsKeySelector =
                new JWSVerificationKeySelector<>(
                        JWSAlgorithm.RS256,
                        jwkSource
                );
        jwtProcessor.setJWSKeySelector(jwsKeySelector);
        jwtProcessor.setJWTClaimsSetVerifier((claims, context) -> {});

        return new NimbusJwtDecoder(jwtProcessor);
    }

    @Bean
    public JwtEncoder jwtEncoder() throws Exception {
        return new NimbusJwtEncoder(jwkSource());
    }
}
