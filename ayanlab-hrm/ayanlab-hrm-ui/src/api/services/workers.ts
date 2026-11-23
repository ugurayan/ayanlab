import { http } from "../client";
import { endpoints } from "../endpoints";
import { unwrapHrmData } from "../hrmApi";

export async function apiListWorkers() {
  const res = await http.get(endpoints.workers.list);
  return unwrapHrmData(res.data);
}

export async function apiUpdateWorker(id: string, patch: Record<string, unknown>) {
  const res = await http.put(endpoints.workers.detail(id), { id, ...patch });
  return unwrapHrmData(res.data);
}