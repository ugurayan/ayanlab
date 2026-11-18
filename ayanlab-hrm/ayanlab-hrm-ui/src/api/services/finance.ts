import { http } from '../client'; import { endpoints } from '../endpoints';
export async function apiListInvoices(){ const res = await http.get(endpoints.finance.invoices); return res.data; }
