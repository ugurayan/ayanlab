import React from "react";
import { cn } from "../../lib/cn";

export function Select({
  className,
  ...props
}: React.SelectHTMLAttributes<HTMLSelectElement>) {
  return (
    <select
      className={cn(
        "w-full rounded-xl border border-line bg-canvas px-3 py-2 text-sm text-ink",
        "outline-none transition-shadow",
        "focus:border-ink focus:ring-2 focus:ring-brand-ring",
        className
      )}
      {...props}
    />
  );
}