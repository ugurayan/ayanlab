import type { AxiosResponse } from "axios";

type ApiBody<T> = {
  success?: boolean;
  data?: T;
  message?: string;
};

export function unwrapData<T>(res: AxiosResponse<ApiBody<T> | T>): T {
  const body = res.data;
  if (body && typeof body === "object" && "data" in body && (body as ApiBody<T>).data !== undefined) {
    return (body as ApiBody<T>).data as T;
  }
  return body as T;
}