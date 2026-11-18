package com.ayanlab.security.support;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class JwtSigningKeys {

    private JwtSigningKeys() {}

    /**
     * HS256+ requires at least 256 bits per RFC 7518. Short dev secrets are hashed with SHA-256.
     */
    public static SecretKey hmacShaKeyFromSecret(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("ayanlabs.jwt.secret must be set");
        }
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            keyBytes = sha256(keyBytes);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private static byte[] sha256(byte[] input) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(input);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}