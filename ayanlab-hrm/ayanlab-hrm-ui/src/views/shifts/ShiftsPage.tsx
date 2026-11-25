import { useState } from "react";
import { StaffingScreen } from "../staffing/StaffingScreen";
import { useShiftInstances, useShiftTemplates, useWorkLocations } from "../../data/hooks/useStaffing";
import { Button } from "../../ui/components/Button";
import { Input } from "../../ui/components/Input";
import { P } from "../../security/permissions";
import { apiErrorMessage, type ShiftInstance, type ShiftTemplate } from "../../api/services/staffing";

export function ShiftsPage() {
  const [tab, setTab] = useState<"templates" | "instances">("templates");
  const templates = useShiftTemplates();
  const instances = useShiftInstances();
  const locations = useWorkLocations();
  const [generateFor, setGenerateFor] = useState<string | null>(null);
  const [from, setFrom] = useState("");
  const [to, setTo] = useState("");
  const [message, setMessage] = useState<string | null>(null);

  return (
    <div className="space-y-4">
      <div className="flex gap-2">
        <Button variant={tab === "templates" ? "primary" : "secondary"} onClick={() => setTab("templates")}>Templates</Button>
        <Button variant={tab === "instances" ? "primary" : "secondary"} onClick={() => setTab("instances")}>Instances</Button>
      </div>
      {message && <div className="text-sm text-slate-700">{message}</div>}
      {tab === "templates" ? (
        <StaffingScreen<ShiftTemplate>
          title="Shift templates"
          subtitle="One template is one schedule at one location, in the Instawork posting shape"
          empty="No shift templates yet."
          read="staffiq:shift_templates:read"
          create="staffiq:shift_templates:create"
          update="staffiq:shift_templates:update"
          remove="staffiq:shift_templates:delete"
          columns={[
            { key: "name", label: "Name" },
            { key: "staffingType", label: "Staffing" },
            { key: "startTime", label: "Start" },
            { key: "endTime", label: "End" },
            { key: "employeesNeeded", label: "People" }
          ]}
          fields={[
            { key: "locationId", label: "Location id" },
            { key: "name", label: "Internal name" },
            { key: "staffingType", label: "Staffing type (SHORT_TERM or LONG_TERM)" },
            { key: "roleId", label: "Role id" },
            { key: "description", label: "Job duties" },
            { key: "startTime", label: "Start time" },
            { key: "endTime", label: "End time" },
            { key: "unpaidBreakMinutes", label: "Unpaid break minutes", type: "number" },
            { key: "wageType", label: "Wage type (HOURLY or DAILY)" },
            { key: "wageAmountMinor", label: "Wage minor units", type: "number" },
            { key: "currency", label: "Currency" },
            { key: "employeesNeeded", label: "People needed", type: "number" },
            { key: "staffSelectionType", label: "Staff selection" },
            { key: "daysOfWeek", label: "Days (mon,tue,...) when recurring" }
          ]}
          blank={() => ({
            locationId: locations.data?.[0]?.id ?? "",
            name: "",
            staffingType: "SHORT_TERM",
            roleId: "",
            description: "",
            startTime: "09:00",
            endTime: "17:00",
            unpaidBreakMinutes: 30,
            wageType: "HOURLY",
            wageAmountMinor: 2500,
            currency: "CHF",
            employeesNeeded: 1,
            staffSelectionType: "smart-assign",
            recurring: false,
            daysOfWeek: "mon,tue,wed,thu,fri"
          })}
          query={templates}
          formExtra={(draft, setDraft) => (
            <label className="flex items-center gap-2 text-sm">
              <input
                type="checkbox"
                checked={Boolean(draft.recurring)}
                onChange={(event) => setDraft({ ...draft, recurring: event.target.checked })}
              />
              Recurring schedule
            </label>
          )}
        />
      ) : (
        <StaffingScreen<ShiftInstance>
          title="Shift instances"
          subtitle="Dated shifts. Create a one-off here, or generate dates from a template."
          empty="No shift instances yet."
          read={P.shiftsRead}
          create={P.shiftsCreate}
          update="staffiq:shifts:update"
          remove="staffiq:shifts:delete"
          columns={[
            { key: "name", label: "Name" },
            { key: "status", label: "Status" },
            { key: "startsAt", label: "Starts" },
            { key: "endsAt", label: "Ends" },
            { key: "employeesNeeded", label: "People" }
          ]}
          fields={[
            { key: "locationId", label: "Location id" },
            { key: "roleId", label: "Role id" },
            { key: "name", label: "Name" },
            { key: "description", label: "Job duties" },
            { key: "startsAt", label: "Starts at", type: "datetime-local" },
            { key: "endsAt", label: "Ends at", type: "datetime-local" },
            { key: "unpaidBreakMinutes", label: "Unpaid break minutes", type: "number" },
            { key: "wageType", label: "Wage type" },
            { key: "wageAmountMinor", label: "Wage minor units", type: "number" },
            { key: "currency", label: "Currency" },
            { key: "employeesNeeded", label: "People needed", type: "number" },
            { key: "staffSelectionType", label: "Staff selection" },
            { key: "status", label: "Status" }
          ]}
          blank={() => {
            const start = new Date(Date.now() + 2 * 60 * 60 * 1000);
            const end = new Date(start.getTime() + 8 * 60 * 60 * 1000);
            const local = (date: Date) => {
              const pad = (n: number) => String(n).padStart(2, "0");
              return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
            };
            return {
              locationId: locations.data?.[0]?.id ?? "",
              roleId: "",
              name: "",
              description: "",
              startsAt: local(start),
              endsAt: local(end),
              unpaidBreakMinutes: 30,
              wageType: "HOURLY",
              wageAmountMinor: 2500,
              currency: "CHF",
              employeesNeeded: 1,
              staffSelectionType: "smart-assign",
              status: "OPEN"
            };
          }}
          query={instances}
        />
      )}
      {tab === "templates" && (templates.data ?? []).length > 0 && (
        <div className="rounded-2xl border border-slate-200 p-4 space-y-3">
          <div className="font-semibold">Generate instances</div>
          <select className="rounded-xl border border-slate-200 px-3 py-2" value={generateFor ?? ""} onChange={(event) => setGenerateFor(event.target.value)}>
            <option value="">Choose a template</option>
            {(templates.data ?? []).map((template) => (
              <option key={template.id} value={template.id}>{template.name}</option>
            ))}
          </select>
          <div className="flex gap-2">
            <Input type="date" value={from} onChange={(event) => setFrom(event.target.value)} />
            <Input type="date" value={to} onChange={(event) => setTo(event.target.value)} />
            <Button
              onClick={async () => {
                if (!generateFor) return;
                try {
                  const created = await templates.generate.mutateAsync({ id: generateFor, from, to });
                  setMessage(`Created ${created.length} shift instances.`);
                  setTab("instances");
                } catch (error) {
                  setMessage(apiErrorMessage(error));
                }
              }}
            >
              Generate
            </Button>
          </div>
        </div>
      )}
    </div>
  );
}
