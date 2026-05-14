package com.ayanlabs.security.service;

import com.ayanlabs.core.config.JwtConfig;
import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.enums.ClientTypeEnum;
import com.ayanlabs.core.entity.*;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtConfig jwtConfig;

    public String generateAccessToken(User user,  CoreContext context,
                                       Set<Role> roles, Set<Permission> permissions) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("aid", context.getAppId() != null ? context.getAppId().toString() : null);     // Application ID
        claims.put("uid", user.getId().toString());     // User ID
        claims.put("pid", context.getProfileId() != null ? context.getProfileId().toString() : null);  // Profile ID
        claims.put("oid", context.getOrgId() != null ? context.getOrgId() .toString() : null);
        claims.put("email", user.getEmail());
        claims.put("phone", user.getMobile());
        claims.put("firstName", user.getFirstName());
        claims.put("lastName", user.getLastName());
        claims.put("platform", context.getClientType() != null ? context.getClientType().name() : ClientTypeEnum.Web.name());
        claims.put("roles", roles != null? roles.stream().map(Role::getId).collect(Collectors.toList()):null);
        claims.put("permissions", permissions != null? permissions.stream().map(Permission::getId).collect(Collectors.toList()):null);

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(user.getId().toString())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() +
                   ( context.getClientType() != null && context.getClientType().equals(ClientTypeEnum.Mobile)
                    ? jwtConfig.getAccessTokenExpiryForMobile()
                        : jwtConfig.getAccessTokenExpiryForWeb())))
            .signWith(getSigningKey())
            .compact();
    }

    public String generateRefreshToken(User user,  CoreContext context) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("aid", context.getAppId() != null ? context.getAppId().toString() : null)
                .claim("uid", user.getId().toString())
                .claim("pid", context.getProfileId() != null ? context.getProfileId().toString() : null)
                .claim("oid", context.getOrgId() != null ? context.getOrgId().toString() : null)
                .claim("type", "refresh")
                .claim("platform", context.getClientType() != null ? context.getClientType().name() : ClientTypeEnum.Web.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getRefreshTokenExpiry()))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    public boolean isTokenValid(String token) {
        try { parseToken(token); return true; }
        catch (JwtException e) { return false; }
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
    }

    /**
     * Build a CoreContext object from a JWT (access or refresh). Extracts known claims and maps platform to ClientTypeEnum.
     */
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

    /**
     * Validate the provided refresh token and, if valid and matching the provided user/context, return a new
     * access and refresh token map {accessToken, refreshToken}.
     * Throws JwtException if token invalid or type mismatch.
     */
    public Map<String, String> refreshTokensIfValid(String refreshToken, User user, CoreContext context, Set<Role> roles, Set<Permission> permissions) {
        // Validate and parse
        Claims claims = parseToken(refreshToken);
        // Ensure it's a refresh token
        String type = (claims.get("type") != null) ? claims.get("type").toString() : "";
        if (!"refresh".equalsIgnoreCase(type)) throw new JwtException("Provided token is not a refresh token");
        // Ensure subject matches user
        String subj = claims.getSubject();
        if (subj == null || !subj.equals(user.getId().toString())) throw new JwtException("Refresh token subject does not match user");
        // Optionally ensure app id matches
        Object aid = claims.get("aid");
        if (aid != null && context.getAppId() != null && !aid.toString().equals(context.getAppId().toString())) {
            throw new JwtException("Refresh token app id does not match context");
        }

        String newAccess = generateAccessToken(user, context, roles, permissions);
        String newRefresh = generateRefreshToken(user, context);
        Map<String, String> out = new HashMap<>();
        out.put("accessToken", newAccess);
        out.put("refreshToken", newRefresh);
        return out;
    }
}
