export type VerificationTaskStatus = "pending" | "reviewing" | "accepted" | "rejected" | "needs_attention" | "needs_interview";

export type VerificationTaskType =
  | "name_surname"
  | "address"
  | "driving_licence"
  | "ssn"
  | "work_permit"
  | "itin"
  | "employer_id"
  | "national_id"
  | "birth_certificate"
  | "utility_bill"
  | "bank_statement"
  | "lease_contract"
  | "green_card"
  | "tax_document"
  | "training_certificate"
  | "education_diploma"
  | "safety_certificate"
  | "criminal_record"
  | "reference_letter"
  | "photo"
  | "other";

export type VerificationTask = {
  id: string;
  workerId: string;
  workerName: string;
  workerEmail: string;
  type: VerificationTaskType;
  category: "identity" | "address" | "workEligibility" | "certificate" | "photo" | "background";
  status: VerificationTaskStatus;
  documentUrls: string[]; // URLs to document images/files
  submittedAt: string;
  assignedTo?: string; // Admin user ID who is reviewing
  reviewedAt?: string;
  reviewedBy?: string;
  reviewNotes?: string;
  priority: "low" | "medium" | "high";
};

export type VerificationRequest = {
  id: string;
  type: "mobile" | "email";
  identifier: string; // phone number or email
  code?: string; // verification code
  codeSentAt?: string;
  codeExpiresAt?: string;
  verifiedAt?: string;
  attempts: number;
  maxAttempts: number;
};

