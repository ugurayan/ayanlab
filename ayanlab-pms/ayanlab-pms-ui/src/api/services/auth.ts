import { authHttp, appKey } from "../pmsClient";
import { endpoints } from "../endpoints";
import type { AuthSession, PmsPersona, User } from "../../types/auth";

const PMS_ROLES: PmsPersona[] = [
  "PROPERTY_MANAGER",
  "ACCOUNTANT",
  "LEASING_AGENT",
  "HOME_OWNER",
  "FIELD_STAFF"
];

type PlatformLoginData = {
  accessToken?: string;
  refreshToken?: string;
  userId?: string;
  orgId?: string;
  user?: {
    email?: string;
    firstName?: string;
    lastName?: string;
  };
  access?: {
    roleCodes?: string[];
    permissionCodes?: string[];
    roles?: string[];
    primaryRoleCode?: string;
  };
  token?: string;
  email?: string;
  displayName?: string;
  roles?: string[];
};

function getLanguage(): string {
  if (typeof window !== "undefined") {
    return window.localStorage.getItem("language") || "en";
  }
  return "en";
}

function mapRoles(codes: string[]): PmsPersona[] {
  const mapped = codes
    .map((c) => c.toUpperCase().replace(/^IPMS_/, "").replace(/-/g, "_"))
    .filter((c): c is PmsPersona => PMS_ROLES.includes(c as PmsPersona));
  return mapped.length ? mapped : ["PROPERTY_MANAGER"];
}

/**
 * Login against ayanlab-platform-backend (`POST /auth/api/login`).
 */
export async function apiLogin(email: string, password: string, language?: string): Promise<AuthSession> {
  const lang = language ?? getLanguage();

  try {
    const res = await authHttp.post(`${endpoints.auth.login}?lang=${lang}`, {
      email,
      password,
      appKey
    });

    if (res.data?.success === false) {
      throw new Error(res.data.message || "Invalid email or password");
    }

    const data: PlatformLoginData = res.data?.data ?? res.data;
    const accessToken = data.accessToken || data.token;
    if (!accessToken) {
      throw new Error("Login failed: no access token");
    }

    if (data.refreshToken) {
      localStorage.setItem("pms.refresh_token", data.refreshToken);
    }

    const roleSource = [
      ...(data.access?.roleCodes ?? []),
      ...(data.access?.roles ?? []),
      ...(data.roles ?? []),
      ...(data.access?.primaryRoleCode ? [data.access.primaryRoleCode] : [])
    ];

    const displayName =
      data.displayName ||
      [data.user?.firstName, data.user?.lastName].filter(Boolean).join(" ") ||
      email;

    const user: User = {
      id: data.userId || "",
      email: data.user?.email || data.email || email,
      displayName,
      roles: mapRoles(roleSource),
      orgId: data.orgId || "",
      orgName: "Organization"
    };

    return { accessToken, user };
  } catch (error: unknown) {
    const err = error as { response?: { data?: { message?: string }; statusText?: string }; request?: unknown; message?: string };
    if (err.response) {
      throw new Error(err.response.data?.message || err.response.statusText || "Login failed");
    }
    if (err.request) {
      throw new Error("No response from auth service. Is platform backend running on :8102?");
    }
    if (err.message) {
      throw new Error(err.message);
    }
    throw new Error("Invalid email or password");
  }
}