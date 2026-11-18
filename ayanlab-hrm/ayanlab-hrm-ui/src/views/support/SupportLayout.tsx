import { Outlet } from "react-router-dom";

/** Layout for support routes; renders nested support pages (Help Center, Cancellation policy, etc.). */
export function SupportLayout() {
  return <Outlet />;
}
