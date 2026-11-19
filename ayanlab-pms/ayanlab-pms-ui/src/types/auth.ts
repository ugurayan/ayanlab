export type PmsPersona =
  | "PROPERTY_MANAGER"
  | "ACCOUNTANT"
  | "LEASING_AGENT"
  | "HOME_OWNER"
  | "FIELD_STAFF";

export type User = {
  id: string;
  email: string;
  displayName: string;
  roles: PmsPersona[];
  orgId: string;
  orgName: string;
};

export type AuthSession = {
  accessToken: string;
  user: User;
};