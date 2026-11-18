import { http } from "../client";
import { endpoints } from "../endpoints";
import type { Shift } from "../../mock/mockDb";

export async function apiListShifts() {
  const res = await http.get(endpoints.shifts.list);
  return res.data;
}

export async function apiCreateShift(input: Omit<Shift, "id" | "status"> & { status?: Shift["status"] }) {
  const res = await http.post(endpoints.shifts.create, input);
  return res.data;
}
