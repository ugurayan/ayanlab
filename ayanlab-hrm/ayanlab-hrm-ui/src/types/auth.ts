// Roles can be either predefined role names or UUIDs from the API
export type Role = string;

// Permissions can be UUIDs from the API
export type Permission = string;

export type TenantScope = {
  orgId?: string;
  locationIds?: string[];
};

export type User = {
  id: string;
  email: string;
  displayName: string;
  roles: Role[];
  permissions: Permission[];
  scope: TenantScope;
  userApproved?: boolean; // If false, user can only access Settings and Logout
  profileApproved?: boolean; // If false, user can only access Settings and Logout
  profileId?: string; // Optional profile ID if user has a profile
};

export type AuthSession = {
  accessToken: string;
  user: User;
};
