import React from "react";
import { Card, CardContent } from "../../ui/components/Card";
import { Button } from "../../ui/components/Button";
import { Badge } from "../../ui/components/Badge";
import type { LucideIcon } from "lucide-react";

export type ProfileSectionCardProps = {
  title: string;
  description: string;
  icon: LucideIcon;
  actionLabel?: string;
  onAction?: () => void;
  /** Optional count or status badge next to title */
  badge?: string;
  badgeTone?: "blue" | "green" | "yellow" | "red" | "gray";
  /** Optional "NEW" or similar tag */
  tag?: string;
  /** Optional list items with counts (e.g. Available: 28) */
  items?: { label: string; count: number; onClick?: () => void }[];
  /** Secondary action (e.g. "Learn more") */
  secondaryActionLabel?: string;
  onSecondaryAction?: () => void;
  children?: React.ReactNode;
};

export function ProfileSectionCard({
  title,
  description,
  icon: Icon,
  actionLabel,
  onAction,
  badge,
  badgeTone = "blue",
  tag,
  items,
  secondaryActionLabel,
  onSecondaryAction,
  children,
}: ProfileSectionCardProps) {
  return (
    <Card>
      <CardContent className="p-5">
        <div className="flex items-start gap-4">
          <div className="flex-shrink-0 w-12 h-12 rounded-xl bg-slate-100 flex items-center justify-center text-slate-500">
            <Icon size={24} strokeWidth={1.5} />
          </div>
          <div className="flex-1 min-w-0">
            <div className="flex flex-wrap items-center gap-2">
              <h3 className="text-base font-semibold text-slate-900">{title}</h3>
              {tag && (
                <Badge tone="green" className="text-[10px] px-1.5 py-0">
                  {tag}
                </Badge>
              )}
              {badge != null && (
                <span className="text-sm text-slate-500">{badge}</span>
              )}
            </div>
            <p className="text-sm text-slate-600 mt-1">{description}</p>
            {items && items.length > 0 && (
              <ul className="mt-3 space-y-1.5">
                {items.map(({ label, count, onClick }) => (
                  <li key={label}>
                    {onClick ? (
                      <button
                        type="button"
                        onClick={onClick}
                        className="flex items-center justify-between w-full text-left text-sm text-slate-700 hover:bg-slate-50 rounded-lg px-2 py-1 -mx-2"
                      >
                        <span>{label}</span>
                        <span className="font-medium">{count}</span>
                      </button>
                    ) : (
                      <div className="flex items-center justify-between text-sm text-slate-700">
                        <span>{label}</span>
                        <span className="font-medium">{count}</span>
                      </div>
                    )}
                  </li>
                ))}
              </ul>
            )}
            {children}
            <div className="flex flex-wrap gap-2 mt-4">
              {actionLabel && onAction && (
                <Button variant="primary" onClick={onAction} className="text-sm">
                  {actionLabel}
                </Button>
              )}
              {secondaryActionLabel && onSecondaryAction && (
                <Button variant="secondary" onClick={onSecondaryAction} className="text-sm">
                  {secondaryActionLabel}
                </Button>
              )}
            </div>
          </div>
        </div>
      </CardContent>
    </Card>
  );
}
