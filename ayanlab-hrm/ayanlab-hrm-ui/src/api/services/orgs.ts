import { http } from "../client";
import { endpoints } from "../endpoints";

export async function apiListOrgs() {
  const res = await http.get(endpoints.orgs.list);
  return res.data;
}
