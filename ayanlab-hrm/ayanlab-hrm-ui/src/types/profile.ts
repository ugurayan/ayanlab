export type ProfileExperience = {
  id: string;
  company: string;
  role: string;
  from: string;  // ISO date or "Jan 2022"
  to?: string;   // undefined = current
};

export type ProfileDocument = {
  id: string;
  name: string;
  type: "cv" | "certificate" | "reference" | "id";
  url: string;
};

export type JobProfileStatus = "approved" | "pending_admin";

export type JobProfile = {
  id: string;
  workerId: string;
  label: string;              // e.g. "Nurse – Night shifts", "Warehouse operative"
  approvalStatus: JobProfileStatus;
};

export type Profile = {
  id: string;
  avatarUrl: string;
  displayName: string;
  firstName: string;
  lastName: string;
  rating: number;
  ratingCount: number;
  email: string;
  phone: string;
  location: string;

  about: string;
  softSkills: string[];     // e.g. ["Teamwork", "Communication"]
  expertise: string[];      // e.g. ["Nursing", "Warehouse"]
  languages: string[];      // e.g. ["English", "German"]
  drivingLicences: string[]; // e.g. ["B", "C"]
  jobRoles: string[];       // preferred roles

  experiences: ProfileExperience[]; // last 3
  documents: ProfileDocument[];
  education: string;
  certificates: string[];

  approvalStatus: "approved" | "pending_admin"; // for latest edits
};

/** Optional fields used for profile strength / completeness (Instawork-style) */
export type ProfileCompleteness = {
  birthdate: string | null;           // ISO date or null
  employmentVerified: boolean;
  quizCompleted: boolean;
  skillAssessmentsCompleted: number;  // count of completed assessments
  resumeImported: boolean;
  attireVerified: boolean;
  w2OnboardingCompleted: boolean;
  /** Position eligibility counts */
  positionsAvailable: number;
  positionsEligible: number;
  positionsNeedReview: number;
  positionsNotEligible: number;
  businessesFavoritedCount: number;
  publicProfileUrl: string | null;
  /** Contribution / streak (optional) */
  contributionStreakWeeks: number;
  lastContributionWeek: string | null;
};