import { http } from '../client'; import { endpoints } from '../endpoints';
export async function apiListWorkers(){ const res = await http.get(endpoints.workers.list); return res.data; }
