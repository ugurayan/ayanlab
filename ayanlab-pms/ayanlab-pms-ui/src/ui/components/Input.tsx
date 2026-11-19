import React from "react";
import { cn } from "../../lib/cn";

const focusRing =
  "focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-teal-600/40 focus-visible:ring-offset-2 focus-visible:ring-offset-[rgb(var(--surface-muted))] dark:focus-visible:ring-teal-400/45";

export const Input = React.forwardRef<HTMLInputElement, React.InputHTMLAttributes<HTMLInputElement>>(
  function Input({ className, ...props }, ref) {
    return (
      <input
        ref={ref}
        className={cn(
          "min-h-[44px] w-full rounded-xl border border-[rgb(var(--border))] bg-[rgb(var(--surface))] px-3 py-2 text-sm text-[rgb(var(--text))] shadow-sm transition placeholder:text-[rgb(var(--text-muted))]/70",
          focusRing,
          className
        )}
        {...props}
      />
    );
  }
);