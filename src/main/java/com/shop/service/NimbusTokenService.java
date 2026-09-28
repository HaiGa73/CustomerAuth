package com.shop.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class NimbusTokenService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    public long getExpirationMs() {
        return expirationMs;
    }

    public String generateToken(UserDetails userDetails) {
        try {
            Date now = new Date();
            Date expiry = new Date(now.getTime() + expirationMs);

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(userDetails.getUsername())
                    .claim("roles", userDetails.getAuthorities().stream().map(Object::toString).toList())
                    .issueTime(now)
                    .expirationTime(expiry)
                    .build();

            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            JWSSigner signer = new MACSigner(secret.getBytes(StandardCharsets.UTF_8));
            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo token qua Nimbus: " + e.getMessage());
        }
    }

    public String extractUsername(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(secret.getBytes(StandardCharsets.UTF_8));

            if (!signedJWT.verify(verifier)) {
                return false;
            }

            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expiration == null || expiration.before(new Date())) {
                return false;
            }

            return userDetails.getUsername().equals(signedJWT.getJWTClaimsSet().getSubject());
        } catch (Exception e) {
            return false;
        }
    }
}