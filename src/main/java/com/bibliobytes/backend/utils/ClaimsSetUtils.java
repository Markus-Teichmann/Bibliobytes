package com.bibliobytes.backend.utils;

import com.nimbusds.jwt.JWTClaimsSet;
import lombok.experimental.UtilityClass;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

@UtilityClass
public class ClaimsSetUtils {
//    public static JwtClaimsSet convert(Map<String, Object> map) {
//        JwtClaimsSet.Builder jwtSetBuilder = JwtClaimsSet.builder();
//        //Converting Claims with Unregistered Claim names
//        for (Map.Entry<String, Object> entry: map.entrySet()) {
//            if (!JWTClaimsSet.getRegisteredNames().contains(entry.getKey())) {
//                jwtSetBuilder.claim(entry.getKey(), entry.getValue());
//            }
//        }
//
//        //Converting Claims with Registered Claim names
//        String issuer = set.getIssuer();
//        if (issuer != null) {
//            jwtSetBuilder.issuer(issuer);
//        }
//        String subject = set.getSubject();
//        if (subject != null) {
//            jwtSetBuilder.subject(subject);
//        }
//        List<String> audience = set.getAudience();
//        if (audience != null) {
//            jwtSetBuilder.audience(audience);
//        }
//        Date expiration = set.getExpirationTime();
//        if (expiration != null) {
//            jwtSetBuilder.expiresAt(Instant.ofEpochSecond(expiration.getTime())); // Instant
//        }
//        Date notBefore = set.getNotBeforeTime();
//        if (notBefore != null) {
//            jwtSetBuilder.notBefore(Instant.ofEpochSecond(notBefore.getTime())); //Instant
//        }
//        Date issuedAt = set.getIssueTime();
//        if (issuedAt != null) {
//            jwtSetBuilder.issuedAt(Instant.ofEpochSecond(issuedAt.getTime())); //Instant
//        }
//        String id = set.getJWTID();
//        if (id != null) {
//            jwtSetBuilder.id(id);
//        }
//
//        return jwtSetBuilder.build();
//    }
}
