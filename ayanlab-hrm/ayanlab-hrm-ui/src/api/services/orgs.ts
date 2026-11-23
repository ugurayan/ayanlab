import { http } from "../client";
import { endpoints } from "../endpoints";
import { unwrapHrmData } from "../hrmApi";

export async function apiListOrgs() {
  const res = await http.get(endpoints.orgs.list);
  return unwrapHrmData(res.data);
}