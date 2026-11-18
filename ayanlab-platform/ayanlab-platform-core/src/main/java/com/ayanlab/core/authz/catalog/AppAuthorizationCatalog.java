package com.ayanlab.core.authz.catalog;

import com.ayanlab.core.util.PermissionCodes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.*;

/**
 * App-owned authorization catalog (schema v2). Imported idempotently by the platform:
 * permissions are {@code appCode:resource:action} for every declared resource/action, policy groups may use
 * wildcard patterns (expanded on import), roles bundle policy groups, users get roles / groups / overrides.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AppAuthorizationCatalog(
    Integer schemaVersion,
    String appCode,
    List<OrganizationSpec> organizations,
    List<ResourceSpec> resources,
    List<PolicyGroupSpec> policyGroups,
    List<RoleSpec> roles,
    List<UserSpec> users
) {

    public static final int SCHEMA_VERSION = 2;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OrganizationSpec(String code, String name, String description) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ResourceSpec(String code, String module, String name, List<String> actions) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PolicyGroupSpec(String code, String name, String description,
                                  List<String> permissions, List<String> excludes) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RoleSpec(String code, String name, String description, List<String> policyGroups) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record UserSpec(String email, String password, String firstName, String lastName, String mobile,
                           String birthDate, String profileSlug, String profileName, String tenancy,
                           String organizationCode, String registrationStatus,
                           List<String> roles, List<String> policyGroups,
                           List<PermissionOverrideSpec> permissionOverrides) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PermissionOverrideSpec(String code, String effect) {}

    public List<OrganizationSpec> organizationsOrEmpty() { return organizations != null ? organizations : List.of(); }
    public List<ResourceSpec> resourcesOrEmpty() { return resources != null ? resources : List.of(); }
    public List<PolicyGroupSpec> policyGroupsOrEmpty() { return policyGroups != null ? policyGroups : List.of(); }
    public List<RoleSpec> rolesOrEmpty() { return roles != null ? roles : List.of(); }
    public List<UserSpec> usersOrEmpty() { return users != null ? users : List.of(); }

    /** Combines catalog fragments for the same app (e.g. permissions.json + roles.json + users.json). */
    public AppAuthorizationCatalog merge(AppAuthorizationCatalog other) {
        if (other == null) {
            return this;
        }
        String mergedApp = appCode != null ? appCode : other.appCode;
        if (appCode != null && other.appCode != null && !appCode.equals(other.appCode)) {
            throw new IllegalArgumentException("Cannot merge catalogs of different apps: " + appCode + " / " + other.appCode);
        }
        return new AppAuthorizationCatalog(
            SCHEMA_VERSION,
            mergedApp,
            concat(organizations, other.organizations),
            concat(resources, other.resources),
            concat(policyGroups, other.policyGroups),
            concat(roles, other.roles),
            concat(users, other.users)
        );
    }

    /** Structural validation; returns human-readable problems (empty when valid). */
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (schemaVersion != null && schemaVersion != SCHEMA_VERSION) {
            errors.add("Unsupported schemaVersion " + schemaVersion);
        }
        if (appCode == null || appCode.isBlank()) {
            errors.add("appCode is required");
            return errors;
        }
        Set<String> declared = new HashSet<>();
        for (ResourceSpec resource : resourcesOrEmpty()) {
            for (String action : resource.actions() != null ? resource.actions() : List.<String>of()) {
                String code = PermissionCodes.of(appCode, resource.code(), action);
                if (!PermissionCodes.isValid(code)) {
                    errors.add("Invalid permission " + code);
                }
                declared.add(code);
            }
        }
        Set<String> groupCodes = new HashSet<>();
        for (PolicyGroupSpec group : policyGroupsOrEmpty()) {
            if (group.code() == null || !groupCodes.add(group.code())) {
                errors.add("Missing or duplicate policy group code: " + group.code());
            }
            for (String pattern : concat(group.permissions(), group.excludes())) {
                if (!PermissionCodes.isValidGrant(pattern) || !pattern.startsWith(appCode + ":")) {
                    errors.add("Policy group " + group.code() + " has invalid pattern " + pattern);
                }
            }
        }
        Set<String> roleCodes = new HashSet<>();
        for (RoleSpec role : rolesOrEmpty()) {
            if (role.code() == null || !roleCodes.add(role.code())) {
                errors.add("Missing or duplicate role code: " + role.code());
            }
            for (String group : role.policyGroups() != null ? role.policyGroups() : List.<String>of()) {
                if (!groupCodes.contains(group)) {
                    errors.add("Role " + role.code() + " references unknown policy group " + group);
                }
            }
        }
        Set<String> orgCodes = new HashSet<>();
        organizationsOrEmpty().forEach(o -> orgCodes.add(o.code()));
        for (UserSpec user : usersOrEmpty()) {
            if (user.email() == null || user.email().isBlank()) {
                errors.add("User without email");
                continue;
            }
            for (String role : user.roles() != null ? user.roles() : List.<String>of()) {
                if (!roleCodes.contains(role)) {
                    errors.add("User " + user.email() + " references unknown role " + role);
                }
            }
            for (String group : user.policyGroups() != null ? user.policyGroups() : List.<String>of()) {
                if (!groupCodes.contains(group)) {
                    errors.add("User " + user.email() + " references unknown policy group " + group);
                }
            }
            for (PermissionOverrideSpec override : user.permissionOverrides() != null ? user.permissionOverrides() : List.<PermissionOverrideSpec>of()) {
                if (!declared.contains(override.code())) {
                    errors.add("User " + user.email() + " overrides undeclared permission " + override.code());
                }
                if (!"GRANT".equals(override.effect()) && !"DENY".equals(override.effect())) {
                    errors.add("User " + user.email() + " has invalid effect " + override.effect());
                }
            }
            if (user.organizationCode() != null && !orgCodes.contains(user.organizationCode())) {
                errors.add("User " + user.email() + " references unknown organization " + user.organizationCode());
            }
        }
        return errors;
    }

    private static <T> List<T> concat(List<T> a, List<T> b) {
        List<T> out = new ArrayList<>();
        if (a != null) out.addAll(a);
        if (b != null) out.addAll(b);
        return out;
    }
}
