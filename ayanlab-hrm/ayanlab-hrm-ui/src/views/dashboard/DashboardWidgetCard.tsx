import React from "react";
import { Card, CardContent, CardHeader, CardTitle } from "../../ui/components/Card";
import { getWidgetData } from "../../mock/dashboardWidgetData";
import type { DashboardWidget } from "../../types/dashboard";
import type { DailyPoint } from "../../mock/dashboardWidgetData";

type DashboardWidgetCardProps = {
  widget: DashboardWidget;
};

/** Renders a single dashboard widget with mock data based on widget id */
export function DashboardWidgetCard({ widget }: DashboardWidgetCardProps) {
  const data = getWidgetData(widget.id) as Record<string, unknown>;
  if (!data) return null;

  const size = widget.defaultSize;
  const gridClass = size.w >= 2 ? "md:col-span-2" : "";

  return (
    <Card className={`${gridClass}`}>
      <CardHeader className="pb-2">
        <CardTitle className="text-base">{widget.name}</CardTitle>
        {widget.description && (
          <p className="text-xs text-slate-500 font-normal">{widget.description}</p>
        )}
      </CardHeader>
      <CardContent className="pt-0">
        <WidgetContent widgetId={widget.id} data={data} />
      </CardContent>
    </Card>
  );
}

function WidgetContent({ widgetId, data }: { widgetId: string; data: Record<string, unknown> }) {
  // Single stat (value + label)
  if (typeof data.value === "number" && data.label) {
    return (
      <div className="space-y-1">
        <div className="text-2xl font-bold text-slate-900">{data.value}</div>
        <div className="text-xs text-slate-500">{String(data.label)}</div>
      </div>
    );
  }

  // Status (e.g. system health)
  if (data.status && data.label) {
    return (
      <div className="flex items-center gap-2">
        <span className="inline-block w-2 h-2 rounded-full bg-emerald-500" />
        <span className="text-sm font-medium">{String(data.status)}</span>
      </div>
    );
  }

  // Jobs created/assigned (today, week, month)
  if (data.today != null && data.lastWeek != null && data.lastMonth != null) {
    return (
      <div className="grid grid-cols-3 gap-2 text-center">
        <div>
          <div className="text-lg font-bold">{Number(data.today)}</div>
          <div className="text-[10px] text-slate-500">Today</div>
        </div>
        <div>
          <div className="text-lg font-bold">{Number(data.lastWeek)}</div>
          <div className="text-[10px] text-slate-500">Last week</div>
        </div>
        <div>
          <div className="text-lg font-bold">{Number(data.lastMonth)}</div>
          <div className="text-[10px] text-slate-500">Last month</div>
        </div>
      </div>
    );
  }

  // Chart (series of daily points)
  if (data.series && Array.isArray(data.series)) {
    const series = data.series as DailyPoint[];
    const max = Math.max(...series.map((d) => d.value), 1);
    return (
      <div className="space-y-2">
        <div className="text-xs text-slate-500">{String(data.title ?? "")}</div>
        <div className="flex items-end gap-0.5 h-24">
          {series.slice(-14).map((d, i) => (
            <div
              key={d.date}
              className="flex-1 min-w-0 rounded-t bg-blue-500/70 hover:bg-blue-500"
              style={{ height: `${(d.value / max) * 100}%` }}
              title={`${d.label ?? d.date}: ${d.value}`}
            />
          ))}
        </div>
        <div className="flex justify-between text-[10px] text-slate-400">
          <span>{series[0]?.label}</span>
          <span>{series[series.length - 1]?.label}</span>
        </div>
      </div>
    );
  }

  // Earnings (totals, breakdown, empty state)
  if (data.breakdown && typeof data.breakdown === "object") {
    const breakdown = data.breakdown as Record<string, number>;
    return (
      <div className="space-y-3">
        <div className="flex justify-between items-baseline">
          <span className="text-xs font-semibold text-slate-500">Totals</span>
          <span className="text-lg font-bold">${Number(data.totals ?? 0).toFixed(2)}</span>
        </div>
        <a href="#" className="text-xs text-blue-600 hover:underline">
          How is this calculated?
        </a>
        <ul className="space-y-1 text-sm">
          <li className="flex justify-between">
            <span>Shifts</span>
            <span>${Number(breakdown.shifts ?? 0).toFixed(2)}</span>
          </li>
          <li className="flex justify-between">
            <span>Top Pro bonuses</span>
            <span>${Number(breakdown.topProBonuses ?? 0).toFixed(2)}</span>
          </li>
          <li className="flex justify-between">
            <span>Tips &amp; promotions</span>
            <span>${Number(breakdown.tipsPromotions ?? 0).toFixed(2)}</span>
          </li>
        </ul>
        {data.emptyMessage ? (
          <p className="text-xs text-slate-500 pt-2">{String(data.emptyMessage)}</p>
        ) : null}
      </div>
    );
  }

  // Recent activity (list of { text, time })
  if (data.items && Array.isArray(data.items) && (data.items as unknown[]).some((x: unknown) => typeof x === "object" && x !== null && "text" in x)) {
    const items = data.items as { id?: string; text: string; time: string }[];
    return (
      <ul className="space-y-2 text-sm">
        {items.slice(0, 5).map((item, i) => (
          <li key={item.id ?? i} className="flex justify-between gap-2">
            <span className="text-slate-700 truncate">{item.text}</span>
            <span className="text-xs text-slate-400 shrink-0">{item.time}</span>
          </li>
        ))}
      </ul>
    );
  }

  // Jobs list (title, company)
  if (data.items && Array.isArray(data.items) && (data.items as unknown[]).some((x: unknown) => typeof x === "object" && x !== null && "title" in x && "company" in x)) {
    const items = data.items as { id: string; title: string; company: string }[];
    return (
      <ul className="space-y-2 text-sm">
        {items.map((item) => (
          <li key={item.id} className="flex justify-between gap-2 border-b border-slate-100 pb-2 last:border-0">
            <div>
              <div className="font-medium text-slate-800">{item.title}</div>
              <div className="text-xs text-slate-500">{item.company}</div>
            </div>
          </li>
        ))}
      </ul>
    );
  }

  // Upcoming shifts (title, location, date)
  if (data.items && Array.isArray(data.items) && (data.items as unknown[]).some((x: unknown) => typeof x === "object" && x !== null && "date" in x && "location" in x)) {
    const items = data.items as { id: string; title: string; location: string; date: string }[];
    return (
      <ul className="space-y-2 text-sm">
        {items.map((item) => (
          <li key={item.id} className="flex justify-between gap-2 border-b border-slate-100 pb-2 last:border-0">
            <div>
              <div className="font-medium text-slate-800">{item.title}</div>
              <div className="text-xs text-slate-500">{item.location} · {item.date}</div>
            </div>
          </li>
        ))}
      </ul>
    );
  }

  // Messages (from, preview, time)
  if (data.items && Array.isArray(data.items) && (data.items as unknown[]).some((x: unknown) => typeof x === "object" && x !== null && "preview" in x && "from" in x)) {
    const items = data.items as { id: string; from: string; preview: string; time: string }[];
    return (
      <ul className="space-y-2 text-sm">
        {items.map((item) => (
          <li key={item.id} className="border-b border-slate-100 pb-2 last:border-0">
            <div className="font-medium text-slate-700">{item.from}</div>
            <div className="text-xs text-slate-600 truncate">{item.preview}</div>
            <div className="text-[10px] text-slate-400">{item.time}</div>
          </li>
        ))}
      </ul>
    );
  }

  // Transaction history (empty or list)
  if (data.emptyMessage && Array.isArray(data.items) && data.items.length === 0) {
    return <p className="text-sm text-slate-500">{String(data.emptyMessage)}</p>;
  }

  // Org: created jobs list, last shifts, not assigned, transaction list
  if (data.items && Array.isArray(data.items)) {
    const items = data.items as Record<string, unknown>[];
    const first = items[0];
    if (!first) return <p className="text-xs text-slate-500">No items</p>;
    if ("status" in first && "created" in first) {
      return (
        <ul className="space-y-2 text-sm">
          {items.map((item: Record<string, unknown>, i) => (
            <li key={String(item.id ?? i)} className="flex justify-between gap-2 border-b border-slate-100 pb-2 last:border-0">
              <div className="font-medium text-slate-800">{String(item.title)}</div>
              <span className="text-xs text-slate-500">{String(item.created)} · {String(item.status)}</span>
            </li>
          ))}
        </ul>
      );
    }
    if ("workers" in first && "date" in first) {
      return (
        <ul className="space-y-2 text-sm">
          {items.map((item: Record<string, unknown>, i) => (
            <li key={String(item.id ?? i)} className="flex justify-between gap-2 border-b border-slate-100 pb-2 last:border-0">
              <div><span className="font-medium text-slate-800">{String(item.title)}</span> · {String(item.date)}</div>
              <span className="text-xs text-slate-500">{Number(item.workers)} workers</span>
            </li>
          ))}
        </ul>
      );
    }
    if ("slots" in first) {
      return (
        <ul className="space-y-2 text-sm">
          {items.map((item: Record<string, unknown>, i) => (
            <li key={String(item.id ?? i)} className="flex justify-between gap-2 border-b border-slate-100 pb-2 last:border-0">
              <div className="font-medium text-slate-800">{String(item.title)}</div>
              <span className="text-xs text-slate-500">{Number(item.slots)} slots</span>
            </li>
          ))}
        </ul>
      );
    }
    if ("amount" in first && "description" in first) {
      return (
        <ul className="space-y-2 text-sm">
          {items.map((item: Record<string, unknown>, i) => (
            <li key={String(item.id ?? i)} className="flex justify-between gap-2 border-b border-slate-100 pb-2 last:border-0">
              <div><span className="text-slate-800">{String(item.description)}</span> · <span className="text-xs text-slate-500">{String(item.date)}</span></div>
              <span className="font-medium">{String(item.amount)}</span>
            </li>
          ))}
        </ul>
      );
    }
    if (typeof first === "string" || ("label" in first && !("title" in first))) {
      return (
        <ul className="flex flex-wrap gap-1">
          {items.map((role: unknown, i) => (
            <li key={i} className="rounded-full bg-slate-100 px-2 py-0.5 text-xs text-slate-700">
              {typeof role === "string" ? role : String((role as Record<string, unknown>).label ?? role)}
            </li>
          ))}
        </ul>
      );
    }
  }

  // Fallback: value or label
  if (data.value !== undefined) {
    return (
      <div className="space-y-1">
        <div className="text-2xl font-bold text-slate-900">{String(data.value)}</div>
        {data.label ? <div className="text-xs text-slate-500">{String(data.label)}</div> : null}
      </div>
    );
  }

  return <div className="text-xs text-slate-500">No data</div>;
}
