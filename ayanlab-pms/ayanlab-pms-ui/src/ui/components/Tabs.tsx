import { cn } from "../../lib/cn";

export function Tabs({
  tabs,
  active,
  onChange
}: {
  tabs: { id: string; label: string }[];
  active: string;
  onChange: (id: string) => void;
}) {
  return (
    <div className="flex flex-wrap gap-1 border-b border-[rgb(var(--border))]" role="tablist">
      {tabs.map((t) => {
        const isActive = active === t.id;
        return (
          <button
            key={t.id}
            type="button"
            role="tab"
            aria-selected={isActive}
            onClick={() => onChange(t.id)}
            className={cn(
              "relative min-h-[44px] rounded-t-xl px-4 py-2 text-sm font-medium transition-colors",
              focusVisible,
              isActive
                ? "text-teal-800 after:absolute after:inset-x-2 after:bottom-0 after:h-0.5 after:rounded-full after:bg-teal-700 dark:text-teal-200 dark:after:bg-teal-400"
                : "text-[rgb(var(--text-muted))] hover:bg-[rgb(var(--surface-muted))] hover:text-[rgb(var(--text))]"
            )}
          >
            {t.label}
          </button>
        );
      })}
    </div>
  );
}

const focusVisible =
  "focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-teal-600/40 focus-visible:ring-offset-2 focus-visible:ring-offset-[rgb(var(--surface-muted))]";