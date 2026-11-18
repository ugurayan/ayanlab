import React from "react";
import { cn } from "../../lib/cn";

export function Select({
  className,
  ...props
}: React.SelectHTMLAttributes<HTMLSelectElement>) {
  return (
    <select
      className={cn(
        "w-full rounded-2xl border border-slate-200 bg-white px-3 py-2 text-sm outline-none ring-slate-300 focus:ring-4",
        className
      )}
      {...props}
    />
  );
}
