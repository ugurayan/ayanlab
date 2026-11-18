import React from "react";
import { Link } from "react-router-dom";
import { Card, CardContent } from "../../ui/components/Card";
import { X, UserX, Clock, CalendarCheck, Heart } from "lucide-react";

const sections = [
  {
    id: "no-show",
    title: "No-show to work",
    icon: UserX,
    description:
      "A no-show constitutes a serious breach of conduct, as it adversely affects all parties involved. Failure to attend a confirmed shift without prior cancellation will result in suspension and removal from future shift assignments.",
  },
  {
    id: "late-cancels",
    title: "Late cancels",
    icon: Clock,
    description:
      "Cancellation of a shift within 24 hours of its scheduled start time is classified as a late cancel. Late cancellations provide insufficient notice for replacement staffing and will result in suspension.",
  },
  {
    id: "missing-confirmation",
    title: "Missing your shift confirmation",
    icon: CalendarCheck,
    description:
      "You are required to confirm your shift the day before it begins. Failure to confirm at least 16 hours prior to the shift start time will result in removal from the shift, and the cancellation will be recorded as a late cancel.",
  },
  {
    id: "life-happens",
    title: "Life happens",
    icon: Heart,
    description:
      "We acknowledge that unforeseen circumstances may require last-minute cancellation. Should you need to cancel a shift, you are requested to do so at the earliest possible opportunity.",
  },
];

export function CancellationPolicyPage() {
  return (
    <div className="max-w-2xl mx-auto space-y-6 pb-12">
      {/* Header with close */}
      <div className="flex items-start justify-between gap-4">
        <div className="flex-1 min-w-0">
          <h1 className="text-2xl font-bold text-slate-900">Cancellation policy</h1>
          <p className="text-sm text-slate-500 mt-1">People are counting on you.</p>
        </div>
        <Link
          to="/app/support"
          className="flex-shrink-0 flex items-center justify-center w-10 h-10 rounded-xl border border-slate-200 text-blue-600 hover:bg-blue-50 hover:text-blue-700"
          aria-label="Close"
        >
          <X size={20} strokeWidth={2} />
        </Link>
      </div>

      {/* Policy sections */}
      <div className="space-y-6">
        {sections.map((section) => {
          const Icon = section.icon;
          return (
            <Card key={section.id}>
              <CardContent className="p-5">
                <div className="flex gap-4">
                  <div className="flex-shrink-0 w-12 h-12 rounded-full bg-slate-100 flex items-center justify-center text-slate-600">
                    <Icon size={24} strokeWidth={1.5} />
                  </div>
                  <div className="flex-1 min-w-0">
                    <h2 className="text-base font-semibold text-slate-900">{section.title}</h2>
                    <p className="text-sm text-slate-600 mt-2 leading-relaxed">
                      {section.description}
                    </p>
                  </div>
                </div>
              </CardContent>
            </Card>
          );
        })}
      </div>
    </div>
  );
}
