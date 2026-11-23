import { http } from "../client";
import { endpoints } from "../endpoints";
import { unwrapHrmData } from "../hrmApi";

export async function apiListDocuments() {
  const res = await http.get(endpoints.documents.list);
  return unwrapHrmData(res.data);
}