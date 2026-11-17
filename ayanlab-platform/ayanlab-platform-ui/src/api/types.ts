export type ApiResponse<T> = {
  correlationId?: string;
  success: boolean;
  message?: string;
  data: T;
  timestamp?: string;
  errors?: unknown;
};

export type PagedResponse<T> = {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
};

export type CurrentSession = {
  userId?: string;
  email?: string;
  firstName?: string;
  lastName?: string;
  avatarUrl?: string;
  appId?: string;
  profileId?: string;
  orgId?: string;
  profiles?: {
    id: string;
    profileSlug?: string;
    organizationId?: string;
    isActive?: boolean;
  }[];
  authorization?: {
    roleCodes?: string[];
    permissionCodes?: string[];
    policyGroupCodes?: string[];
  };
};

export type TokenResponse = {
  accessToken: string;
  refreshToken?: string;
  expiresTokenAt?: number;
  userId?: string;
  appId?: string;
  profileId?: string;
  orgId?: string;
};