export type AccountApprovalStatus = "approved" | "pending_admin";

export type LanguageLevel = {
  code: string;
  level: "basic" | "intermediate" | "advanced" | "native";
};

export type AccountSettings = {
  profileName: string;
  company: string;
  title: string;
  notes: string;
  email: string;
  phone: string;
  country: string;
  language: string;
  languages: LanguageLevel[];
  isEmailVerified: boolean;
  isPhoneVerified: boolean;
  isDocumentsVerified: boolean;
  isCertificatesVerified: boolean;
  workPermit?: string;
  drivingLicense?: string;
  itin?: string;
  employerId?: string;
  ssn?: string;
  approvalStatus: AccountApprovalStatus;
};
