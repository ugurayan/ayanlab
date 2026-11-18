import React from "react";
import { Navigate, useLocation } from "react-router-dom";
import type { Permission } from "../types/auth";
import { useAuth } from "../state/auth";

export function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { isAuthed, user } = useAuth();
  const loc = useLocation();
  if (!isAuthed) return <Navigate to="/login" replace state={{ from: loc.pathname }} />;
  
  // If profile is not approved, only allow access to settings page
  const isProfileApproved = user?.profileApproved !== false; // Default to true if not set
  if (!isProfileApproved && !loc.pathname.startsWith("/app/settings")) {
    return <Navigate to="/app/settings" replace />;
  }
  
  return <>{children}</>;
}

export function RequirePermission({ anyOf, children }: { anyOf: Permission[]; children: React.ReactNode }) {
  const { has } = useAuth();
  const ok = anyOf.some((p) => has(p));
  if (!ok) return <Navigate to="/forbidden" replace />;
  return <>{children}</>;
}
