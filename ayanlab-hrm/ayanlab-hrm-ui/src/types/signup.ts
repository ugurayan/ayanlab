export type SignupType = "business" | "employer" | "worker" | "admin";

export type AdminSignupData = {
  // Step 1: User Details
  firstName: string;
  lastName: string;
  birthday: string;
  ssn: string;
  itin?: string;
  email: string;
  phone: string;
  password: string;
};

export type BusinessSignupData = {
  // Step 1: User Details
  firstName: string;
  lastName: string;
  birthday: string;
  ssn: string;
  itin?: string;
  email: string;
  phone: string;
  password: string;
  // Step 2: Organization Type
  organizationType: "new" | "existing";
  existingOrgId?: string; // if existing organization
  // Step 3: Company Information (only if new)
  companyName?: string;
  companyTaxId?: string;
  itin?: string;
  legalEntityType?: string; // e.g., AG, GmbH, LLC
  officialAddress?: string;
  billingAddress?: string; // if different from official
  vatNumber?: string;
  registrationNumber?: string;
  // Step 4
  industry: string;
  jobRoles: string[];
  hearAboutUs: string;
};

export type EmployerSignupData = {
  // Step 1: User Details
  firstName: string;
  lastName: string;
  birthday: string;
  ssn: string;
  itin?: string;
  email: string;
  phone: string;
  password: string;
  // Step 2
  industry: string;
  jobRoles: string[];
  hearAboutUs: string;
};

export type WorkerSignupData = {
  // Step 1
  firstName: string;
  lastName: string;
  birthday: string;
  ssn: string;
  itin?: string;
  email: string;
  phone: string;
  password: string;
  // Step 2
  industry: string;
  jobRoles: string[];
  experiences: Array<{
    jobRoleId: string;
    years: number;
    months: number;
    days: number;
  }>;
  // Step 3
  drivingLicense?: {
    number: string;
    issueDate: string;
    expiryDate: string;
    frontImage?: File;
    backImage?: File;
  };
  // Step 4
  certificates?: Array<{
    id?: string; // predefined certificate ID
    name: string; // certificate name (from predefined or custom)
    file: File;
    expiryDate?: string;
  }>;
  // Step 5
  documents?: Array<{
    id?: string; // predefined document ID
    type: string; // document type (from predefined or custom)
    frontImage?: File; // for ID documents
    backImage?: File; // for ID documents
    file?: File; // for other documents
  }>;
  // Step 5
  address?: {
    street: string;
    city: string;
    state: string;
    zipCode: string;
    country: string;
  };
  // Step 6
  generalAvailability?: {
    days: string[]; // e.g., ["monday", "tuesday"]
    timeWindows: Array<{
      day: string;
      startTime: string;
      endTime: string;
    }>;
  };
  // Step 7
  customShifts?: Array<{
    date: string;
    startTime: string;
    endTime: string;
    notes?: string;
  }>;
};

