package com.bibliobytes.backend.auth.config;

import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

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

    public static void generateRSAKey() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();
        System.out.println("---Start Public Key ---");
        System.out.println(Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
        System.out.println("---End Public Key ---");
        System.out.println("---Start Private Key ---");
        System.out.println(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
        System.out.println("---End Private Key ---");
    }
}
