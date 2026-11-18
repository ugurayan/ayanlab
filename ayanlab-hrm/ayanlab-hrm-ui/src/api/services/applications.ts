import { http } from '../client'; import { endpoints } from '../endpoints';
export async function apiListApplications(){ const res = await http.get(endpoints.applications.list); return res.data; }
