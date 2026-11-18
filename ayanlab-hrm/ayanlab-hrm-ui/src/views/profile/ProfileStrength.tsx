import React from "react";
import { Card, CardContent } from "../../ui/components/Card";
import type { Profile, ProfileCompleteness } from "../../types/profile";

export type ProfileStrengthProps = {
  profile: Profile;
  completeness: ProfileCompleteness;
};

const ITEMS: { key: string; label: string; check: (p: Profile, c: ProfileCompleteness) => boolean }[] = [
  { key: "birthdate", label: "Birthdate", check: (_, c) => !!c.birthdate },
  { key: "workExperience", label: "Work experience", check: (p) => p.experiences.length > 0 },
  { key: "employmentVerified", label: "Employment verified", check: (_, c) => c.employmentVerified },
  { key: "quiz", label: "Quiz completed", check: (_, c) => c.quizCompleted },
  { key: "skillAssessment", label: "Skill assessment", check: (_, c) => c.skillAssessmentsCompleted > 0 },
  { key: "language", label: "Language added", check: (p) => p.languages.length > 0 },
  { key: "education", label: "Education", check: (p) => !!p.education?.trim() },
  { key: "resume", label: "Resume imported", check: (_, c) => c.resumeImported },
  { key: "attire", label: "Attire verified", check: (_, c) => c.attireVerified },
  { key: "certificates", label: "Certificates", check: (p) => p.certificates.length > 0 },
];

function computeScore(profile: Profile, completeness: ProfileCompleteness): { score: number; total: number; completed: number } {
  const total = ITEMS.length;
  const completed = ITEMS.filter(({ check }) => check(profile, completeness)).length;
  const score = total === 0 ? 0 : Math.round((completed / total) * 100);
  return { score, total, completed };
}

export function ProfileStrength({ profile, completeness }: ProfileStrengthProps) {
  const { score, total, completed } = computeScore(profile, completeness);

  return (
    <Card>
      <CardContent className="p-5">
        <div className="flex items-center justify-between gap-4 mb-3">
          <span className="text-sm font-semibold text-slate-700">Profile strength</span>
          <span className="text-sm font-bold text-slate-900">{score}%</span>
        </div>
        <div className="h-2.5 w-full rounded-full bg-slate-200 overflow-hidden">
          <div
            className="h-full rounded-full bg-emerald-500 transition-all duration-300"
            style={{ width: `${score}%` }}
          />
        </div>
        <p className="text-xs text-slate-500 mt-2">
          {completed} of {total} items complete. Add more details to strengthen your profile.
        </p>
      </CardContent>
    </Card>
  );
}
