import { http } from "../client";
import { endpoints } from "../endpoints";
import { unwrapHrmData } from "../hrmApi";

export async function apiListApplications() {
  const res = await http.get(endpoints.applications.list);
  return unwrapHrmData(res.data);
}