import { apiListInvoicesFromFinanceRecords } from "./hrmResources";

export async function apiListInvoices() {
  return apiListInvoicesFromFinanceRecords();
}