package com.ayanlabs.security.service;

import com.ayanlabs.core.config.JwtConfig;
import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.enums.ClientTypeEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import com.ayanlabs.security.support.JwtSigningKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.UUID;

/**
 * JWT parse/validate only — safe for product backends (no JPA entities).
 */
@Service
@RequiredArgsConstructor
public class JwtTokenParser {

    private final JwtConfig jwtConfig;

    public Claims parseToken(String token) {
        return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    public boolean isTokenValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public CoreContext buildCoreContextFromToken(String token) {
        Claims c = parseToken(token);
        CoreContext.CoreContextBuilder builder = CoreContext.builder();
        try {
            Object aid = c.get("aid");
            if (aid != null) builder.appId(UUID.fromString(aid.toString()));
        } catch (Exception ignored) {}
        try {
            Object uid = c.get("uid");
            if (uid != null) builder.userId(UUID.fromString(uid.toString()));
        } catch (Exception ignored) {}
        try {
            Object pid = c.get("pid");
            if (pid != null) builder.profileId(UUID.fromString(pid.toString()));
        } catch (Exception ignored) {}
        try {
            Object oid = c.get("oid");
            if (oid != null) builder.orgId(UUID.fromString(oid.toString()));
        } catch (Exception ignored) {}
        try {
            Object plat = c.get("platform");
            if (plat != null) builder.clientType(ClientTypeEnum.valueOf(plat.toString()));
        } catch (Exception ignored) {
            builder.clientType(ClientTypeEnum.Web);
        }
        return builder.token(token).build();
    }

    private SecretKey getSigningKey() {
        return JwtSigningKeys.hmacShaKeyFromSecret(jwtConfig.getSecret());
    }
}