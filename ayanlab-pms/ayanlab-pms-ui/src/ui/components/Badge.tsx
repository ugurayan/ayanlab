import React from "react";
import { cn } from "../../lib/cn";

export function Badge({
  className,
  tone = "neutral",
  children
}: {
  className?: string;
  tone?: "neutral" | "warn" | "ok" | "danger";
  children: React.ReactNode;
}) {
  const tones = {
    neutral:
      "border border-[rgb(var(--border))] bg-[rgb(var(--surface-muted))] text-[rgb(var(--text))]",
    warn: "border border-amber-200/80 bg-amber-50 text-amber-950 dark:border-amber-800/60 dark:bg-amber-950/50 dark:text-amber-100",
    ok: "border border-emerald-200/80 bg-emerald-50 text-emerald-950 dark:border-emerald-800/60 dark:bg-emerald-950/40 dark:text-emerald-100",
    danger: "border border-rose-200/80 bg-rose-50 text-rose-950 dark:border-rose-800/60 dark:bg-rose-950/40 dark:text-rose-100"
  };
  return (
    <span className={cn("rounded-full px-2.5 py-0.5 text-xs font-medium", tones[tone], className)}>{children}</span>
  );
}