import { http } from "../client";
import { endpoints } from "../endpoints";
import { unwrapHrmData } from "../hrmApi";
import type { Shift } from "../../mock/mockDb";

export async function apiListShifts() {
  const res = await http.get(endpoints.shifts.list);
  return unwrapHrmData<Shift[]>(res.data);
}

export async function apiCreateShift(input: Omit<Shift, "id" | "status"> & { status?: Shift["status"] }) {
  const res = await http.post(endpoints.shifts.create, { ...input, status: input.status ?? "DRAFT" });
  return unwrapHrmData<Shift>(res.data);
}