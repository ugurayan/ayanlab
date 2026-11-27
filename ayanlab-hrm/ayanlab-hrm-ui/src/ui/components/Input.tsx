import React from "react";
import { cn } from "../../lib/cn";

export const Input = React.forwardRef<
  HTMLInputElement,
  React.InputHTMLAttributes<HTMLInputElement>
>(function Input({ className, ...props }, ref) {
  return (
    <input
      ref={ref}
      className={cn(
        "w-full rounded-xl border border-line bg-canvas px-3 py-2 text-sm text-ink",
        "placeholder:text-ink-subtle",
        "outline-none transition-shadow",
        "focus:border-ink focus:ring-2 focus:ring-brand-ring",
        className
      )}
      {...props}
    />
  );
});