import React from "react";
import { cn } from "../../lib/cn";

type Variant = "primary" | "secondary" | "ghost" | "danger";
type Size = "sm" | "md";

const variants: Record<Variant, string> = {
  primary:
    "bg-teal-800 text-white shadow-sm hover:bg-teal-700 active:bg-teal-900 dark:bg-teal-600 dark:hover:bg-teal-500 dark:active:bg-teal-700",
  secondary:
    "border border-[rgb(var(--border))] bg-[rgb(var(--surface))] text-[rgb(var(--text))] shadow-sm hover:bg-[rgb(var(--surface-muted))] active:bg-[rgb(var(--border))]/30",
  ghost:
    "bg-transparent text-[rgb(var(--text))] hover:bg-[rgb(var(--surface-muted))] active:bg-[rgb(var(--border))]/25",
  danger:
    "bg-rose-600 text-white shadow-sm hover:bg-rose-700 active:bg-rose-800 dark:bg-rose-700 dark:hover:bg-rose-600"
};

const sizes: Record<Size, string> = {
  sm: "min-h-[36px] px-3 py-1.5 text-xs",
  md: "min-h-[44px] px-4 py-2 text-sm"
};

const focusRing =
  "focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-teal-600/40 focus-visible:ring-offset-2 focus-visible:ring-offset-[rgb(var(--surface-muted))] dark:focus-visible:ring-teal-400/45";

export function Button({
  variant = "primary",
  size = "md",
  className,
  isLoading,
  disabled,
  children,
  ...props
}: React.ButtonHTMLAttributes<HTMLButtonElement> & { variant?: Variant; size?: Size; isLoading?: boolean }) {
  return (
    <button
      disabled={disabled || isLoading}
      className={cn(
        "inline-flex items-center justify-center gap-2 rounded-xl font-semibold transition duration-150 ease-out disabled:pointer-events-none disabled:opacity-50",
        focusRing,
        sizes[size],
        variants[variant],
        className
      )}
      {...props}
    >
      {isLoading && (
        <span
          className="h-3.5 w-3.5 animate-spin rounded-full border-2 border-current border-t-transparent"
          aria-hidden
        />
      )}
      <span className={isLoading ? "opacity-80" : undefined}>{children}</span>
    </button>
  );
}