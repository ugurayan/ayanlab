/** Unwrap {@code ApiResponse<T>} from ayanlab-hrm-backend. */
export type HrmApiEnvelope<T> = {
  success?: boolean;
  data?: T;
  message?: string;
};

export function unwrapHrmData<T>(payload: unknown): T {
  if (payload == null) {
    return [] as T;
  }
  if (typeof payload === "object" && payload !== null && "data" in payload) {
    const inner = (payload as HrmApiEnvelope<T>).data;
    return (inner ?? []) as T;
  }
  return payload as T;
}