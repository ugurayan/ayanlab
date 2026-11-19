import type { ReactNode } from "react";
import { Link } from "react-router-dom";
import { ArrowLeft } from "lucide-react";

export function PortalShell({ title, children }: { title: string; children: ReactNode }) {
  return (
    <div className="mx-auto max-w-3xl space-y-4">
      <div className="flex items-center gap-2 text-sm">
        <Link to="/app/dashboard" className="inline-flex items-center gap-1 text-teal-800 hover:underline">
          <ArrowLeft size={16} /> Admin
        </Link>
        <span className="text-[rgb(var(--text-muted))]/80">/</span>
        <span className="font-semibold text-[rgb(var(--text))]">{title} preview</span>
      </div>
      <div className="rounded-2xl border border-[rgb(var(--border))] bg-[rgb(var(--surface-elevated))] p-4 shadow-soft dark:shadow-none md:p-6">
        {children}
      </div>
    </div>
  );
}