import type { ReactNode } from "react";
import { cn } from "../../lib/cn";

export function EmptyState({
  title,
  description,
  action,
  className
}: {
  title: string;
  description?: string;
  action?: ReactNode;
  className?: string;
}) {
  return (
    <div
      className={cn(
        "flex flex-col items-center justify-center rounded-xl border border-dashed border-[rgb(var(--border))] bg-[rgb(var(--surface-muted))]/50 px-6 py-12 text-center",
        className
      )}
    >
      <h2 className="text-base font-semibold text-[rgb(var(--text))]">{title}</h2>
      {description && (
        <p className="mt-2 max-w-sm text-sm text-[rgb(var(--text-muted))]">{description}</p>
      )}
      {action && <div className="mt-6">{action}</div>}
    </div>
  );
}