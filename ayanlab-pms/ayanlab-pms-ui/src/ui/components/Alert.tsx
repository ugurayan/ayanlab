import type { ReactNode } from "react";
import { cn } from "../../lib/cn";

type Tone = "info" | "warn" | "danger" | "success";

const toneClass: Record<Tone, string> = {
  info: "border-[rgb(var(--border))] bg-[rgb(var(--surface-muted))] text-[rgb(var(--text))]",
  warn: "border-amber-200/80 bg-amber-50 text-amber-950 dark:border-amber-800/60 dark:bg-amber-950/40 dark:text-amber-100",
  danger: "border-rose-200/80 bg-rose-50 text-rose-950 dark:border-rose-800/60 dark:bg-rose-950/40 dark:text-rose-100",
  success:
    "border-emerald-200/80 bg-emerald-50 text-emerald-950 dark:border-emerald-800/60 dark:bg-emerald-950/40 dark:text-emerald-100"
};

export function Alert({
  tone = "info",
  title,
  children,
  className
}: {
  tone?: Tone;
  title?: string;
  children: ReactNode;
  className?: string;
}) {
  return (
    <div
      role="status"
      className={cn("rounded-xl border px-4 py-3 text-sm", toneClass[tone], className)}
    >
      {title && <p className="mb-1 font-semibold">{title}</p>}
      <div className="text-[rgb(var(--text-muted))] [&_p]:m-0 dark:text-inherit">{children}</div>
    </div>
  );
}