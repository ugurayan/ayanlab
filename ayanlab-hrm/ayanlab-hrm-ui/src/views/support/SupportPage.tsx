import React, { useMemo, useState } from "react";
import { Input } from "../../ui/components/Input";
import { Button } from "../../ui/components/Button";

const DEPARTMENTS: { id: string; label: string; types: string[] }[] = [
  {
    id: "finance",
    label: "Finance",
    types: ["Payment hold", "Invoice question", "Refund request", "Billing address change"]
  },
  {
    id: "employer",
    label: "Employer situations",
    types: [
      "Late arrival / no-show",
      "Shift cancellation",
      "Performance concern",
      "Contract / paperwork issue"
    ]
  },
  {
    id: "technical",
    label: "Technical support",
    types: ["Cannot log in", "App not loading", "Error messages", "Feature request"]
  }
];

export function SupportPage() {
  const [name, setName] = useState("");
  const [departmentId, setDepartmentId] = useState<string>(DEPARTMENTS[0]?.id ?? "finance");
  const [type, setType] = useState<string>("");
  const [subject, setSubject] = useState("");
  const [message, setMessage] = useState("");

  const currentDept = useMemo(
    () => DEPARTMENTS.find((d) => d.id === departmentId) ?? DEPARTMENTS[0],
    [departmentId]
  );

  const canSubmit = name.trim() && subject.trim() && message.trim();

  return (
    <div className="max-w-3xl mx-auto">
      <div className="mb-4">
        <div className="text-2xl font-semibold text-slate-900">Contact support</div>
      </div>

      <div className="rounded-3xl border border-slate-200 bg-white p-6 md:p-8">
        <div className="mb-4">
          <div className="text-sm font-semibold text-slate-900">Submit your request</div>
          <div className="text-xs text-slate-500 mt-1">
            Your request will be processed and our support staff will get back to you in 24 hours.
          </div>
        </div>

        <div className="space-y-4 text-sm">
          <div>
            <div className="text-xs text-slate-500 mb-1">Name</div>
            <Input
              placeholder="Your name"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
          </div>

          <div className="grid gap-4 md:grid-cols-2">
            <div>
              <div className="text-xs text-slate-500 mb-1">Department</div>
              <select
                className="w-full rounded-2xl border border-slate-200 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-slate-900/5"
                value={departmentId}
                onChange={(e) => {
                  setDepartmentId(e.target.value);
                  setType("");
                }}
              >
                {DEPARTMENTS.map((d) => (
                  <option key={d.id} value={d.id}>
                    {d.label}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <div className="text-xs text-slate-500 mb-1">Issue type</div>
              <select
                className="w-full rounded-2xl border border-slate-200 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-slate-900/5"
                value={type}
                onChange={(e) => setType(e.target.value)}
              >
                <option value="">Select an issue</option>
                {currentDept.types.map((t) => (
                  <option key={t} value={t}>
                    {t}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div>
            <div className="text-xs text-slate-500 mb-1">Subject</div>
            <Input
              placeholder="Short summary of your request"
              value={subject}
              onChange={(e) => setSubject(e.target.value)}
            />
          </div>

          <div>
            <div className="text-xs text-slate-500 mb-1">Message</div>
            <textarea
              className="w-full min-h-[160px] rounded-2xl border border-slate-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-slate-900/5"
              placeholder="Describe the issue, including any relevant shifts, workers or invoices..."
              value={message}
              onChange={(e) => setMessage(e.target.value)}
            />
          </div>

          <div className="flex items-center justify-end gap-3 pt-2 text-sm">
            <button
              type="button"
              className="px-3 py-2 text-slate-500 hover:text-slate-700"
              onClick={() => {
                setName("");
                setDepartmentId(DEPARTMENTS[0]?.id ?? "finance");
                setType("");
                setSubject("");
                setMessage("");
              }}
            >
              Cancel
            </button>
            <Button disabled={!canSubmit}>Send request (mock)</Button>
          </div>
        </div>
      </div>
    </div>
  );
}
