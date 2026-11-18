package com.ayanlab.security.service;

import com.ayanlab.core.config.JwtConfig;
import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.data.enums.ClientTypeEnum;
import com.ayanlab.core.data.dto.EffectiveAuthorization;
import com.ayanlab.core.entity.*;
import com.ayanlab.core.provider.ResourceScopeClaims;
import com.ayanlab.security.authz.AuthzVersionChecker;
import io.jsonwebtoken.*;
import com.ayanlab.security.support.JwtSigningKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtConfig jwtConfig;
    private final com.ayanlab.security.service.JwtTokenParser jwtTokenParser;

    public String generateAccessToken(User user, CoreContext context,
                                      Set<Role> roles, Set<Permission> permissions) {
        Set<String> roleCodes = roles != null
            ? roles.stream().map(Role::getCode).filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new))
            : Set.of();
        Set<String> permissionCodes = permissions != null
            ? permissions.stream().map(Permission::getCode).filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new))
            : Set.of();
        return generateAccessToken(user, context, roleCodes, permissionCodes, Set.of());
    }

    public String generateAccessToken(User user, CoreContext context, EffectiveAuthorization authorization) {
        if (authorization == null) {
            return generateAccessToken(user, context, Set.<Role>of(), Set.<Permission>of());
        }
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("appKey", authorization.getAppCode());
        extraClaims.put(ResourceScopeClaims.CLAIM, ResourceScopeClaims.toClaim(authorization.getResourceScopes()));
        if (authorization.getAuthzVersion() != null) {
            extraClaims.put(AuthzVersionChecker.CLAIM, authorization.getAuthzVersion());
        }
        return buildAccessToken(
            user,
            context,
            authorization.getRoleCodes() != null ? authorization.getRoleCodes() : Set.of(),
            authorization.getPermissionCodes() != null ? authorization.getPermissionCodes() : Set.of(),
            authorization.getPolicyGroupCodes() != null ? authorization.getPolicyGroupCodes() : Set.of(),
            extraClaims
        );
    }

    public String generateAccessToken(User user, CoreContext context,
                                      Set<String> roleCodes, Set<String> permissionCodes, Set<String> policyGroupCodes) {
        return buildAccessToken(user, context, roleCodes, permissionCodes, policyGroupCodes, Map.of());
    }

    private String buildAccessToken(User user, CoreContext context,
                                    Set<String> roleCodes, Set<String> permissionCodes, Set<String> policyGroupCodes,
                                    Map<String, Object> extraClaims) {
        Map<String, Object> claims = new HashMap<>(extraClaims);
        claims.put("aid", context.getAppId() != null ? context.getAppId().toString() : null);
        claims.put("uid", user.getId().toString());
        claims.put("pid", context.getProfileId() != null ? context.getProfileId().toString() : null);
        claims.put("oid", context.getOrgId() != null ? context.getOrgId().toString() : null);
        claims.put("profileId", context.getProfileId() != null ? context.getProfileId().toString() : null);
        claims.put("orgId", context.getOrgId() != null ? context.getOrgId().toString() : null);
        claims.put("email", user.getEmail());
        claims.put("phone", user.getMobile());
        claims.put("firstName", user.getFirstName());
        claims.put("lastName", user.getLastName());
        claims.put("platform", context.getClientType() != null ? context.getClientType().name() : ClientTypeEnum.Web.name());
        claims.put("roles", roleCodes != null ? new ArrayList<>(roleCodes) : List.of());
        claims.put("permissions", permissionCodes != null ? new ArrayList<>(permissionCodes) : List.of());
        claims.put("policyGroups", policyGroupCodes != null ? new ArrayList<>(policyGroupCodes) : List.of());

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(user.getId().toString())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiryMillis(context)))
            .signWith(getSigningKey())
            .compact();
    }

    /** @deprecated use {@link #generateAccessToken(User, CoreContext, Set, Set, Set)} with permission codes */
    @Deprecated
    public String generateAccessTokenLegacy(User user, CoreContext context,
                                       Set<Role> roles, Set<Permission> permissions) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("aid", context.getAppId() != null ? context.getAppId().toString() : null);
        claims.put("uid", user.getId().toString());
        claims.put("pid", context.getProfileId() != null ? context.getProfileId().toString() : null);
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
            .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiryMillis(context)))
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
                .setExpiration(new Date(System.currentTimeMillis() + secondsToMillis(jwtConfig.getRefreshTokenExpiry())))
                .signWith(getSigningKey())
                .compact();
    }

    /** Config values are seconds; {@link Date} expiry offset must be milliseconds. */
    private long accessTokenExpiryMillis(CoreContext context) {
        long seconds = context.getClientType() != null && context.getClientType().equals(ClientTypeEnum.Mobile)
                ? jwtConfig.getAccessTokenExpiryForMobile()
                : jwtConfig.getAccessTokenExpiryForWeb();
        return secondsToMillis(seconds);
    }

    private static long secondsToMillis(long seconds) {
        return seconds * 1000L;
    }

    public Claims parseToken(String token) {
        return jwtTokenParser.parseToken(token);
    }

    public boolean isTokenValid(String token) {
        return jwtTokenParser.isTokenValid(token);
    }

    private SecretKey getSigningKey() {
        return JwtSigningKeys.hmacShaKeyFromSecret(jwtConfig.getSecret());
    }

    /**
     * Build a CoreContext object from a JWT (access or refresh). Extracts known claims and maps platform to ClientTypeEnum.
     */
    public CoreContext buildCoreContextFromToken(String token) {
        return jwtTokenParser.buildCoreContextFromToken(token);
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
