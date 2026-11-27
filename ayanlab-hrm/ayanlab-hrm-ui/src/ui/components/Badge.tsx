import React from "react";
import { cn } from "../../lib/cn";

type Tone = "gray" | "green" | "amber" | "red" | "blue" | "yellow" | "brand";

const tones: Record<Tone, string> = {
  gray: "bg-surface text-ink-muted ring-1 ring-line",
  green: "bg-emerald-50 text-emerald-800 ring-1 ring-emerald-100",
  amber: "bg-amber-50 text-amber-900 ring-1 ring-amber-100",
  red: "bg-rose-50 text-rose-800 ring-1 ring-rose-100",
  blue: "bg-sky-50 text-sky-800 ring-1 ring-sky-100",
  yellow: "bg-yellow-50 text-yellow-900 ring-1 ring-yellow-100",
  brand: "bg-brand-soft text-brand ring-1 ring-brand-ring"
};

export function Badge({
  tone = "gray",
  className,
  ...props
}: React.HTMLAttributes<HTMLSpanElement> & { tone?: Tone }) {
  return (
    <span
      className={cn(
        "inline-flex items-center rounded-md px-2 py-0.5 text-xs font-semibold",
        tones[tone],
        className
      )}
      {...props}
    />
  );
}