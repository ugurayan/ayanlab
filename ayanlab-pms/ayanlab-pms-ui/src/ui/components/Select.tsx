import React from "react";
import { cn } from "../../lib/cn";
import { fieldSelectClass, fieldSelectSmClass } from "../../lib/uiClasses";

export const Select = React.forwardRef<
  HTMLSelectElement,
  React.SelectHTMLAttributes<HTMLSelectElement> & { density?: "sm" | "md" }
>(function Select({ className, density = "md", children, ...props }, ref) {
  return (
    <select
      ref={ref}
      className={cn(density === "sm" ? fieldSelectSmClass : fieldSelectClass, className)}
      {...props}
    >
      {children}
    </select>
  );
});