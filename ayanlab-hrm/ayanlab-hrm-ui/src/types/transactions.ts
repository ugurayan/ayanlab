export type TransactionDirection = "debit" | "credit"; // debit = money out, credit = money in

export type TransactionStatus = "pending" | "completed" | "failed" | "refunded";

export type PaymentType = "bank_transfer" | "credit_card" | "debit_card" | "paypal" | "other";

export type Transaction = {
  id: string;
  workerId?: string; // for worker-related payments
  orgId?: string; // for employer/org
  amountCents: number;
  currency: string;
  direction: TransactionDirection;
  status: TransactionStatus;
  description: string;
  relatedInvoiceId?: string;
  externalReferenceId?: string; // External payment reference
  jobId?: string; // Related job/shift
  jobTitle?: string; // Job title for display
  shiftId?: string; // Related shift
  paymentType: PaymentType;
  bankName?: string; // Bank name for bank transfers
  bankAccountLast4?: string; // Last 4 digits of bank account
  creditCardLast4?: string; // Last 4 digits of credit card
  creditCardBrand?: string; // Visa, Mastercard, etc.
  feesCents: number; // Transaction fees
  createdAt: string;
  processedAt?: string;
  // Admin-only fields
  createdBy?: string; // User ID who created
  createdByName?: string; // Display name
  updatedBy?: string; // User ID who last updated
  updatedByName?: string; // Display name
  updatedAt?: string; // Last update timestamp
};
