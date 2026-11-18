export type JobType = "temporary" | "permanent";
export type JobSchedule = "full-time" | "part-time";
export type JobWorkMode = "on-site" | "hybrid" | "remote";

export type JobRole = {
  id: string;
  nameKey: string; // Translation key for role name
  descriptionKey: string; // Translation key for role description
  requiredSkills: string[]; // Array of skill IDs
};

export type Skill = {
  id: string;
  nameKey: string; // Translation key for skill name
  category: string; // Skill category
};

export type ExpertiseLevel = {
  value: number;
  unit: "years" | "months" | "days";
  labelKey: string; // Translation key for display
};

export type DressCode = {
  id: string;
  nameKey: string;
  descriptionKey: string;
  imageUrl?: string;
  items: ClothingItem[];
};

export type ClothingItem = {
  id: string;
  nameKey: string;
  colorOptions?: string[];
};

export type ClothingItemMeta = {
  id: string;
  summaryKey: string;
  imageUrl?: string;
};

export type ContractType = {
  id: string;
  nameKey: string; // Translation key for contract type name
};

export type ContactType = {
  id: string;
  nameKey: string;
};

export type FreeTimeWindow = {
  value: number;
  unit: "minutes" | "hours";
  labelKey: string;
};

export type Certificate = {
  id: string;
  nameKey: string;
  descriptionKey?: string;
  roleIds: string[];
};

export type JobDocument = {
  id: string;
  nameKey: string;
  descriptionKey?: string;
  roleIds: string[];
};

export type JobTemplate = {
  id: string;
  orgId?: string; // if undefined -> predefined/global template
  name: string;
  description?: string;
  title: string;
  roleId: string;
  location: string;
  address?: string;
  workLoad?: string;
  contractTypeId?: string;
  contactTypeId?: string;
  freeTimeWindowValue?: number;
  freeTimeWindowUnit?: FreeTimeWindow["unit"];
  expertiseLabelKey?: string;
  dressCodeId?: string;
  requiredSkillIds: string[];
  certificateIds?: string[];
  documentIds?: string[];
};

export type Job = {
  id: string;
  title: string;
  company: string;
  location: string;
  distanceLabel?: string; // e.g. "57.9km"
  address?: string;
  referenceCode?: string;
  logoText?: string; // simple text logo (e.g. H&M, MIGROS) for now
  type: JobType;
  schedule: JobSchedule;
  workloadLabel: string; // e.g. "80 - 100%"
  salaryLabel?: string; // e.g. "CHF 30 per hour"
  contractLabel?: string; // e.g. "Temporary contract"
  startDateLabel?: string; // e.g. "Start on 4 Jan"
  endDateLabel?: string; // e.g. "End on 31 Dec"
  experienceLabel?: string; // e.g. "0 years"
  description: string;
  venueMapLabel?: string; // simple venue text, no real map
  roleId?: string;
  contactTypeId?: string;
  freeTimeWindow?: FreeTimeWindow;
  expertiseLevel?: ExpertiseLevel;
  dressCodeId?: string;
};
