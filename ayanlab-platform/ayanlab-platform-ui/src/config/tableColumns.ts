import type { EntityResource } from "./entities";

const HIDDEN = new Set([
  "password",
  "mfaSecret",
  "passwordResetToken",
  "passwordResetExpiration",
  "emailConfirmationCode",
  "emailCodeExpiration",
  "mobileVerificationCode",
  "mobileCodeExpiration",
]);

/** Preferred visible order: identity, labels, important fields, audit. */
const PRIORITY = [
  "id",
  "code",
  "name",
  "title",
  "description",
  "firstName",
  "lastName",
  "email",
  "mobile",
  "profileSlug",
  "profileName",
  "userId",
  "appCode",
  "module",
  "resource",
  "action",
  "isActive",
  "createdAt",
  "updatedAt",
];

const GENERIC_DEFAULTS = ["id", "code", "name", "description", "isActive", "createdAt", "updatedAt"];

export const USER_COLUMNS = {
  defaults: ["id", "firstName", "lastName", "email", "mobile", "isActive", "createdAt", "updatedAt"],
  all: [
    "id",
    "firstName",
    "lastName",
    "email",
    "mobile",
    "isActive",
    "appId",
    "avatarUrl",
    "birthDate",
    "fullNameVerified",
    "birthdateVerified",
    "isEmailVerified",
    "emailVerificationDate",
    "emailCodeStatus",
    "isEmailCommunicationValid",
    "emailAlternative",
    "mobileCountryCode",
    "isMobileVerified",
    "mobileVerificationDate",
    "mobileCodeStatus",
    "isMobileCommunicationValid",
    "mobileAlternative",
    "mfaEnabled",
    "lastLoginAt",
    "passwordExpirationDate",
    "createdAt",
    "updatedAt",
    "createdBy",
    "updatedBy",
  ],
  labels: {
    firstName: "First name",
    lastName: "Last name",
    email: "Email",
    mobile: "Phone",
    isActive: "Active",
    id: "Id",
    appId: "App id",
    avatarUrl: "Avatar",
    birthDate: "Birth date",
    fullNameVerified: "Name verified",
    birthdateVerified: "Birthdate verified",
    isEmailVerified: "Email verified",
    emailVerificationDate: "Email verified at",
    emailCodeStatus: "Email code status",
    isEmailCommunicationValid: "Email comm. valid",
    emailAlternative: "Alt. email",
    mobileCountryCode: "Phone country",
    isMobileVerified: "Phone verified",
    mobileVerificationDate: "Phone verified at",
    mobileCodeStatus: "Phone code status",
    isMobileCommunicationValid: "Phone comm. valid",
    mobileAlternative: "Alt. phone",
    mfaEnabled: "MFA",
    lastLoginAt: "Last login",
    passwordExpirationDate: "Password expires",
    createdAt: "Created",
    updatedAt: "Updated",
    createdBy: "Created by",
    updatedBy: "Updated by",
  } as Record<string, string>,
};

function orderKeys(keys: Iterable<string>): string[] {
  const set = new Set(keys);
  const head = PRIORITY.filter((k) => set.has(k));
  const rest = [...set].filter((k) => !PRIORITY.includes(k)).sort();
  return [...head, ...rest];
}

export function humanizeColumn(key: string, labels?: Record<string, string>): string {
  if (labels?.[key]) return labels[key];
  return key
    .replace(/([A-Z])/g, " $1")
    .replace(/^./, (s) => s.toUpperCase())
    .trim();
}

export function catalogFor(resource: EntityResource, sampleRows: Record<string, unknown>[]): {
  defaults: string[];
  all: string[];
  labels: Record<string, string>;
} {
  const available = new Set<string>(resource.columns?.length ? resource.columns : GENERIC_DEFAULTS);
  if (resource.id === "users") {
    USER_COLUMNS.all.forEach((c) => available.add(c));
    GENERIC_DEFAULTS.filter((c) => ["code", "name", "description"].includes(c)).forEach((c) =>
      available.delete(c),
    );
  }
  for (const row of sampleRows) {
    for (const k of Object.keys(row)) {
      if (!HIDDEN.has(k)) available.add(k);
    }
  }
  const all = orderKeys(available);
  const defaults =
    resource.id === "users"
      ? USER_COLUMNS.defaults.filter((c) => available.has(c))
      : resource.columns?.length
        ? resource.columns.filter((c) => available.has(c))
        : PRIORITY.filter((k) => available.has(k));
  return {
    defaults: defaults.length ? defaults : GENERIC_DEFAULTS,
    all,
    labels: resource.id === "users" ? USER_COLUMNS.labels : {},
  };
}
