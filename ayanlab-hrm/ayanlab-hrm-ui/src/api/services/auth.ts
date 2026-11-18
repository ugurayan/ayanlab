import { authHttp, appKey } from "../client";
import { endpoints } from "../endpoints";
import type { AuthSession, Role, User } from "../../types/auth";
import { withMainRoleIds } from "../../data/constants/mainRoles";

const getLanguage = (): string => {
  if (typeof window !== "undefined") {
    return window.localStorage.getItem("language") || "en";
  }
  return "en";
};

type PlatformLoginData = {
  accessToken?: string;
  refreshToken?: string;
  userId?: string;
  profileId?: string;
  orgId?: string;
  user?: {
    email?: string;
    firstName?: string;
    lastName?: string;
    registrationStatus?: string;
  };
  access?: {
    roleCodes?: string[];
    permissionCodes?: string[];
    roles?: string[];
    primaryRoleCode?: string;
    primaryRole?: string;
    frontendRole?: string;
  };
  additionalInfo?: {
    frontendRole?: string;
  };
  // legacy / alternate shapes
  token?: string;
  email?: string;
  displayName?: string;
  roles?: string[];
  permissions?: string[];
};

/**
 * Login against ayanlab-platform-backend (`POST /auth/api/login`).
 * Afterwards HRM screens keep using mocks for business data (`VITE_USE_MOCKS`).
 */
export async function apiLogin(
  emailOrMobile: string,
  password: string,
  language?: string
): Promise<AuthSession> {
  const lang = language || getLanguage();

  try {
    const res = await authHttp.post(
      `${endpoints.auth.login}?lang=${lang}`,
      {
        email: emailOrMobile,
        password,
        appKey
      }
    );

    if (!res?.data) {
      throw new Error("Invalid response from server");
    }

    if (res.data.success === false) {
      throw new Error(res.data.message || "Invalid email or password");
    }

    const data: PlatformLoginData = res.data.data ?? res.data;
    const accessToken = data.accessToken || data.token;
    if (!accessToken) {
      throw new Error("Login failed: No token received");
    }

    if (data.refreshToken) {
      localStorage.setItem("refreshToken", data.refreshToken);
    }

    const roleCodes: Role[] = Array.isArray(data.access?.roleCodes)
      ? (data.access!.roleCodes as Role[])
      : Array.isArray(data.access?.roles)
        ? (data.access!.roles as Role[])
        : Array.isArray(data.roles)
          ? (data.roles as Role[])
          : data.access?.primaryRoleCode
            ? [data.access.primaryRoleCode as Role]
            : data.access?.primaryRole
              ? [data.access.primaryRole as Role]
              : data.additionalInfo?.frontendRole
                ? [String(data.additionalInfo.frontendRole).toUpperCase() as Role]
                : [];

    const displayName =
      data.displayName ||
      [data.user?.firstName, data.user?.lastName].filter(Boolean).join(" ") ||
      emailOrMobile;

    const email = data.user?.email || data.email || emailOrMobile;

    const permissions = Array.isArray(data.access?.permissionCodes) ? data.access!.permissionCodes : [];

    const user: User = {
      id: data.userId || "",
      email,
      displayName,
      roles: withMainRoleIds(roleCodes),
      permissions,
      scope: {
        orgId: data.orgId || undefined
      },
      userApproved: true,
      profileApproved: data.user?.registrationStatus !== "Pending",
      profileId: data.profileId || undefined
    };

    return {
      accessToken,
      user
    };
  } catch (error: any) {
    console.error("Login error:", error);

    if (error.response) {
      const errorMessage =
        error.response.data?.message || error.response.statusText || "Login failed";
      throw new Error(errorMessage);
    }
    if (error.request) {
      throw new Error("No response from server. Is platform backend running on :8102?");
    }
    if (error.message) {
      throw new Error(error.message);
    }
    throw new Error("Invalid email or password");
  }
}

export async function apiMe(): Promise<User> {
  const res = await authHttp.get("/auth/api/me");
  const body = res.data?.data ?? res.data;
  const authorization = body?.authorization ?? body?.access;
  const roleCodes: Role[] = Array.isArray(authorization?.roleCodes) ? authorization.roleCodes : [];
  return {
    id: body?.userId || body?.user?.id || "",
    email: body?.email || body?.user?.email || "",
    displayName:
      [body?.firstName ?? body?.user?.firstName, body?.lastName ?? body?.user?.lastName]
        .filter(Boolean)
        .join(" ") || "",
    roles: withMainRoleIds(roleCodes),
    permissions: Array.isArray(authorization?.permissionCodes) ? authorization.permissionCodes : [],
    scope: { orgId: body?.orgId },
    userApproved: true,
    profileApproved: true,
    profileId: body?.profileId
  };
}
