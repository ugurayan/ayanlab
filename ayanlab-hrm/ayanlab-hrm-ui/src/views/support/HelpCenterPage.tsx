import React, { useState } from "react";
import { Link } from "react-router-dom";
import { SupportPage } from "./SupportPage";

const sections = [
  { id: "guidelines", label: "Guidelines", description: "How to use the platform and best practices." },
  { id: "faqs", label: "FAQs", description: "Most commonly asked questions." },
  { id: "support", label: "Support", description: "Contact our support team." }
] as const;

export function HelpCenterPage() {
  const [active, setActive] = useState<(typeof sections)[number]["id"]>("support");

  return (
    <div className="grid gap-6 lg:grid-cols-[260px_1fr]">
      <aside className="space-y-4">
        <div>
          <div className="text-2xl font-bold">Help Center</div>
          <div className="text-sm text-slate-500">Find answers and contact support.</div>
        </div>

        <nav className="mt-2 flex flex-col gap-1">
          {sections.map((s) => (
            <button
              key={s.id}
              type="button"
              onClick={() => setActive(s.id)}
              className={`flex flex-col items-start rounded-2xl px-3 py-2 text-left text-sm border ${
                active === s.id
                  ? "border-slate-900 bg-slate-900 text-white"
                  : "border-transparent text-slate-700 hover:bg-slate-50"
              }`}
            >
              <span className="font-semibold">{s.label}</span>
              <span className="text-xs text-slate-400 line-clamp-2">{s.description}</span>
            </button>
          ))}
        </nav>
      </aside>

      <section className="space-y-4">
        {active === "guidelines" && (
          <div className="rounded-3xl border border-slate-200 bg-white p-6 md:p-8 text-sm text-slate-700 space-y-4">
            <div className="text-lg font-semibold text-slate-900">Platform guidelines</div>
            <p>
              Use this workspace to manage jobs, workers, shifts and documents. Only share information that is
              necessary for staffing and payroll purposes.
            </p>
            <ul className="list-disc pl-5 space-y-1 text-sm">
              <li>Keep worker data accurate and up to date.</li>
              <li>Use the Jobs section to publish clear roles, schedules and dress codes.</li>
              <li>Use the Support tab to report technical issues or compliance concerns.</li>
            </ul>
            <div className="pt-2">
              <Link
                to="/app/support/cancellation-policy"
                className="text-sm font-medium text-blue-600 hover:text-blue-700 hover:underline"
              >
                Cancellation policy →
              </Link>
            </div>
          </div>
        )}

        {active === "faqs" && (
          <div className="rounded-3xl border border-slate-200 bg-white p-6 md:p-8 text-sm text-slate-700 space-y-4">
            <div className="text-lg font-semibold text-slate-900">Frequently asked questions</div>
            <div>
              <div className="text-sm font-semibold text-slate-900">How do I create a new job?</div>
              <div className="text-xs text-slate-600 mt-1">Go to Jobs → Create job and fill in the role, wage, hours and dress code.</div>
            </div>
            <div>
              <div className="text-sm font-semibold text-slate-900">Where can I update my profile or settings?</div>
              <div className="text-xs text-slate-600 mt-1">Use the Settings and My Profile sections in the main navigation.</div>
            </div>
            <div>
              <div className="text-sm font-semibold text-slate-900">How do I contact support?</div>
              <div className="text-xs text-slate-600 mt-1">Switch to the Support tab in this Help Center and submit a request.</div>
            </div>
          </div>
        )}

        {active === "support" && <SupportPage />}
      </section>
    </div>
  );
}
