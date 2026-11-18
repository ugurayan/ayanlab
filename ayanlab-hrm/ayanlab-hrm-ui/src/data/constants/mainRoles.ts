export type MainRoleId =
  | "4a3207bf-cc00-46db-8db7-42e8d9568b8b" // System Admin
  | "a0fdb0b8-a079-4315-ae21-31e68cd80778" // Staff
  | "4b029d24-e658-4a65-8d76-ddbc2627a05c" // Business Admin
  | "055978b5-7e39-494d-9725-a0dca32dda6c" // Business Member
  | "b0633699-641c-47c5-89c6-6a88f7d83754" // Employer
  | "9c2b8be8-bdec-4484-96b6-a2145b654b4d" // Worker
  | "e0cd0911-c905-436e-b729-6e9be593f4dd"; // Guest

export type MainRole = {
  id: MainRoleId;
  /** Role code issued by the platform (ayanlab-hrm-backend authz/roles.json). */
  code: string;
  name: string;
  description: string;
};

export const MAIN_ROLES: MainRole[] = [
  {
    id: "4a3207bf-cc00-46db-8db7-42e8d9568b8b",
    code: "SYSTEM_ADMIN",
    name: "System Admin",
    description: "For system-level admin operations."
  },
  {
    id: "a0fdb0b8-a079-4315-ae21-31e68cd80778",
    code: "STAFF",
    name: "Staff",
    description: "Staff member with elevated privileges."
  },
  {
    id: "4b029d24-e658-4a65-8d76-ddbc2627a05c",
    code: "BUSINESS_ADMIN",
    name: "Business Admin",
    description: "Administrative access to organizations and teams."
  },
  {
    id: "055978b5-7e39-494d-9725-a0dca32dda6c",
    code: "BUSINESS_MEMBER",
    name: "Business Member",
    description: "Member access to organizations and teams."
  },
  {
    id: "b0633699-641c-47c5-89c6-6a88f7d83754",
    code: "EMPLOYER",
    name: "Employer",
    description: "Standard employer with some business access. Only one person that hire a job."
  },
  {
    id: "9c2b8be8-bdec-4484-96b6-a2145b654b4d",
    code: "WORKER",
    name: "Worker",
    description: "Standard user with basic access."
  },
  {
    id: "e0cd0911-c905-436e-b729-6e9be593f4dd",
    code: "GUEST",
    name: "Guest",
    description: "Limited access for anonymous users."
  }
];

export const MAIN_ROLE_IDS = MAIN_ROLES.map((r) => r.id);

export const MAIN_ROLE_BY_ID: Record<MainRoleId, MainRole> = MAIN_ROLES.reduce(
  (acc, role) => {
    acc[role.id] = role;
    return acc;
  },
  {} as Record<MainRoleId, MainRole>
);

/** Adds the main-role ids for any main-role codes so id-based UI checks keep working. */
export function withMainRoleIds(roleCodes: string[]): string[] {
  const ids = MAIN_ROLES.filter((r) => roleCodes.includes(r.code)).map((r) => r.id);
  return [...new Set([...roleCodes, ...ids])];
}
