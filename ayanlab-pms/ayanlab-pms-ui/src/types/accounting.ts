export type AccountType = "ASSET" | "LIABILITY" | "EQUITY" | "INCOME" | "EXPENSE";

export type ChartAccount = {
  id: string;
  code: string;
  name: string;
  type: AccountType;
  parentId?: string;
};

export type GlTransaction = {
  id: string;
  date: string;
  description: string;
  amountCents: number;
  scope: "PROPERTY" | "COMPANY";
  propertyName?: string;
  kind: "CHARGE" | "PAYMENT" | "TRANSFER";
};

export type BankAccount = {
  id: string;
  name: string;
  last4: string;
  balanceCents: number;
};

export type BankLine = {
  id: string;
  bankAccountId: string;
  date: string;
  description: string;
  amountCents: number;
  matched: boolean;
};

export type Bill = {
  id: string;
  vendor: string;
  dueDate: string;
  amountCents: number;
  status: "DUE" | "PAID";
};

export type RecurringTransaction = {
  id: string;
  label: string;
  amountCents: number;
  cadence: "MONTHLY" | "QUARTERLY";
};

export type EftApproval = {
  id: string;
  payee: string;
  amountCents: number;
  status: "PENDING" | "APPROVED";
};

export type BudgetLine = {
  category: string;
  plannedCents: number;
  actualCents: number;
};