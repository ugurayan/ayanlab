import React, { useEffect, useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "../../ui/components/Card";
import { Badge } from "../../ui/components/Badge";
import { Button } from "../../ui/components/Button";
import { Select } from "../../ui/components/Select";
import { JobProfile, Profile, ProfileCompleteness } from "../../types/profile";
import {
  Mail,
  Phone,
  MapPin,
  Star,
  Edit2,
  Check,
  X,
  FileText,
  Calendar,
  Briefcase,
  BadgeCheck,
  HelpCircle,
  ClipboardCheck,
  Languages,
  GraduationCap,
  FileUp,
  Shirt,
  BookOpen,
  FileCheck,
  Users,
  Flame,
  Award,
  Share2,
} from "lucide-react";
import { useJobProfiles } from "../../data/hooks";
import { ProfileStrength } from "./ProfileStrength";
import { ProfileSectionCard } from "./ProfileSectionCard";

const mockProfile: Profile = {
  id: "u1",
  avatarUrl: "https://i.pravatar.cc/120?img=3",
  displayName: "Jane Doe",
  firstName: "Jane",
  lastName: "Doe",
  rating: 4.6,
  ratingCount: 128,
  email: "jane.doe@example.com",
  phone: "+1 555 123 456",
  location: "San Francisco, CA",

  about:
    "Experienced staffing specialist with a focus on healthcare and hospitality. Passionate about matching the right talent to the right shifts.",
  softSkills: ["Teamwork", "Communication", "Problem Solving", "Reliability"],
  expertise: ["Nursing", "Warehouse", "Hospitality"],
  languages: ["English", "German"],
  drivingLicences: ["B"],
  jobRoles: ["Registered Nurse", "Care Assistant", "Warehouse Operative"],

  experiences: [
    {
      id: "exp1",
      company: "City Hospital",
      role: "Registered Nurse",
      from: "Jan 2022",
      to: "Present"
    },
    {
      id: "exp2",
      company: "Global Staffing Ltd",
      role: "Staffing Coordinator",
      from: "Jun 2019",
      to: "Dec 2021"
    },
    {
      id: "exp3",
      company: "Hotel Plaza",
      role: "Front Desk Associate",
      from: "Feb 2017",
      to: "May 2019"
    }
  ],

  documents: [
    { id: "d1", name: "Curriculum Vitae", type: "cv", url: "#" },
    { id: "d2", name: "Nursing License", type: "certificate", url: "#" },
    { id: "d3", name: "Reference – City Hospital", type: "reference", url: "#" }
  ],

  education: "B.Sc. in Nursing – University of Health Sciences, 2016",
  certificates: ["Advanced Cardiac Life Support (ACLS)", "Basic Life Support (BLS)"],

  approvalStatus: "approved"
};

const mockCompleteness: ProfileCompleteness = {
  birthdate: null,
  employmentVerified: false,
  quizCompleted: false,
  skillAssessmentsCompleted: 0,
  resumeImported: false,
  attireVerified: false,
  w2OnboardingCompleted: false,
  positionsAvailable: 28,
  positionsEligible: 7,
  positionsNeedReview: 0,
  positionsNotEligible: 0,
  businessesFavoritedCount: 3,
  publicProfileUrl: "https://app.staffiq.example.com/users/jane-doe/profile",
  contributionStreakWeeks: 0,
  lastContributionWeek: null,
};

export function ProfilePage() {
  const [profile, setProfile] = useState<Profile>(mockProfile);
  const [draft, setDraft] = useState<Profile>(mockProfile);
  const [completeness, setCompleteness] = useState<ProfileCompleteness>(mockCompleteness);
  const [isEdit, setIsEdit] = useState(false);
  const [isProfilePickerOpen, setIsProfilePickerOpen] = useState(false);
  const [selectedJobProfileId, setSelectedJobProfileId] = useState<string | null>(null);

  // In real app: workerId would come from authenticated user
  const workerId = "w_1";
  const jobProfilesQuery = useJobProfiles(workerId);

  const jobProfiles = (jobProfilesQuery.data as JobProfile[] | undefined) ?? [];
  const currentJobProfile = jobProfiles.find((p) => p.id === selectedJobProfileId) ?? jobProfiles[0];

  useEffect(() => {
    if (!jobProfiles.length) return;
    if (!selectedJobProfileId) {
      setSelectedJobProfileId(jobProfiles[0].id);
    }
  }, [jobProfiles, selectedJobProfileId]);

  const startEdit = () => {
    setDraft(profile);
    setIsEdit(true);
  };

  const cancelEdit = () => {
    setDraft(profile);
    setIsEdit(false);
  };

  const saveEdit = () => {
    // In real app: POST draft to backend for approval
    setProfile({ ...draft, approvalStatus: "pending_admin" });
    setIsEdit(false);
  };

  const updateField = <K extends keyof Profile>(key: K, value: Profile[K]) => {
    setDraft((prev) => ({ ...prev, [key]: value }));
  };

  const updateStringList = (key: keyof Profile, value: string) => {
    const items = value
      .split(",")
      .map((v) => v.trim())
      .filter(Boolean);
    updateField(key as any, items as any);
  };

  const updateExperience = <K extends keyof Profile["experiences"][number]>(
    id: string,
    key: K,
    value: Profile["experiences"][number][K]
  ) => {
    setDraft((prev) => ({
      ...prev,
      experiences: prev.experiences.map((exp) =>
        exp.id === id ? { ...exp, [key]: value } : exp
      )
    }));
  };

  const addExperience = () => {
    setDraft((prev) => ({
      ...prev,
      experiences: [
        ...prev.experiences,
        {
          id: `exp_${Date.now()}`,
          company: "",
          role: "",
          from: "",
          to: ""
        }
      ]
    }));
  };

  const removeExperience = (id: string) => {
    setDraft((prev) => ({
      ...prev,
      experiences: prev.experiences.filter((exp) => exp.id !== id)
    }));
  };

  const updateDocument = <K extends keyof Profile["documents"][number]>(
    id: string,
    key: K,
    value: Profile["documents"][number][K]
  ) => {
    setDraft((prev) => ({
      ...prev,
      documents: prev.documents.map((doc) =>
        doc.id === id ? { ...doc, [key]: value } : doc
      )
    }));
  };

  const addDocument = () => {
    setDraft((prev) => ({
      ...prev,
      documents: [
        ...prev.documents,
        {
          id: `doc_${Date.now()}`,
          name: "",
          type: "cv",
          url: ""
        }
      ]
    }));
  };

  const removeDocument = (id: string) => {
    setDraft((prev) => ({
      ...prev,
      documents: prev.documents.filter((doc) => doc.id !== id)
    }));
  };

  const target = isEdit ? draft : profile;

  return (
    <div className="space-y-6 pb-24">
      {/* Header with edit / status */}
      <div className="flex items-center justify-between">
        <div>
          <div className="text-2xl font-bold">Profile</div>
          <div className="text-sm text-slate-500">
            View and request changes to your public worker profile.
          </div>
          {currentJobProfile && (
            <div className="mt-1 text-xs text-slate-500">
              Active job profile: <span className="font-semibold">{currentJobProfile.label}</span>
            </div>
          )}
        </div>
        <div className="flex items-center gap-3">
          {(currentJobProfile?.approvalStatus ?? profile.approvalStatus) === "pending_admin" && (
            <Badge tone="yellow">Changes pending admin approval</Badge>
          )}
          {!isEdit ? (
            <Button variant="secondary" onClick={startEdit}>
              <Edit2 size={16} className="mr-1" />
              Edit profile
            </Button>
          ) : (
            <div className="flex gap-2">
              <Button variant="secondary" onClick={cancelEdit}>
                <X size={16} className="mr-1" />
                Cancel
              </Button>
              <Button onClick={saveEdit}>
                <Check size={16} className="mr-1" />
                Save &amp; submit
              </Button>
            </div>
          )}
        </div>
      </div>

      {/* First: worker profile (avatar, contact, about, skills, experience, documents, education) */}
      <div className="grid gap-6 lg:grid-cols-[280px_1fr]">
        {/* Left: basic info */}
        <div className="space-y-4">
          <Card>
            <CardContent className="p-5 flex flex-col items-center text-center gap-4">
              <img
                src={target.avatarUrl}
                alt={target.displayName}
                className="h-24 w-24 rounded-full object-cover border border-slate-200"
              />
              <div>
                {isEdit ? (
                  <>
                    <input
                      className="w-full rounded-xl border border-slate-200 px-3 py-1 text-sm mb-1"
                      value={draft.displayName}
                      onChange={(e) => updateField("displayName", e.target.value)}
                    />
                    <div className="flex gap-2 mt-1">
                      <input
                        className="w-1/2 rounded-xl border border-slate-200 px-3 py-1 text-xs"
                        value={draft.firstName}
                        onChange={(e) => updateField("firstName", e.target.value)}
                        placeholder="First name"
                      />
                      <input
                        className="w-1/2 rounded-xl border border-slate-200 px-3 py-1 text-xs"
                        value={draft.lastName}
                        onChange={(e) => updateField("lastName", e.target.value)}
                        placeholder="Last name"
                      />
                    </div>
                  </>
                ) : (
                  <>
                    <div className="text-lg font-semibold">{target.displayName}</div>
                    <div className="text-xs text-slate-500">
                      {target.firstName} {target.lastName}
                    </div>
                  </>
                )}
              </div>

              <div className="flex items-center gap-1 text-sm text-slate-700">
                <Star className="text-yellow-400 fill-yellow-400" size={16} />
                <span>{target.rating.toFixed(1)}</span>
                <span className="text-xs text-slate-400">
                  ({target.ratingCount} ratings)
                </span>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Contact details</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3 text-sm">
              <div className="flex items-center gap-2">
                <Mail size={14} className="text-slate-400" />
                {isEdit ? (
                  <input
                    className="w-full rounded-xl border border-slate-200 px-2 py-1 text-sm"
                    value={draft.email}
                    onChange={(e) => updateField("email", e.target.value)}
                  />
                ) : (
                  <span>{target.email}</span>
                )}
              </div>
              <div className="flex items-center gap-2">
                <Phone size={14} className="text-slate-400" />
                {isEdit ? (
                  <input
                    className="w-full rounded-xl border border-slate-200 px-2 py-1 text-sm"
                    value={draft.phone}
                    onChange={(e) => updateField("phone", e.target.value)}
                  />
                ) : (
                  <span>{target.phone}</span>
                )}
              </div>
              <div className="flex items-center gap-2">
                <MapPin size={14} className="text-slate-400" />
                {isEdit ? (
                  <input
                    className="w-full rounded-xl border border-slate-200 px-2 py-1 text-sm"
                    value={draft.location}
                    onChange={(e) => updateField("location", e.target.value)}
                  />
                ) : (
                  <span>{target.location}</span>
                )}
              </div>
            </CardContent>
          </Card>

          <Button
            variant="secondary"
            className="w-full justify-center"
            onClick={() => setIsProfilePickerOpen(true)}
          >
            Select job profile
          </Button>
        </div>

        {/* Right: sections */}
        <div className="space-y-6">
          {/* About */}
          <Card>
            <CardHeader>
              <CardTitle>About</CardTitle>
            </CardHeader>
            <CardContent>
              {isEdit ? (
                <textarea
                  className="w-full rounded-2xl border border-slate-200 px-3 py-2 text-sm min-h-[100px]"
                  value={draft.about}
                  onChange={(e) => updateField("about", e.target.value)}
                />
              ) : (
                <p className="text-sm text-slate-700 whitespace-pre-line">{target.about}</p>
              )}
            </CardContent>
          </Card>

          {/* Soft skills & Expertise */}
          <Card>
            <CardHeader>
              <CardTitle>Skills & Expertise</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4 text-sm">
              <div>
                <div className="text-xs font-semibold text-slate-500 mb-1">Soft skills</div>
                {isEdit ? (
                  <input
                    className="w-full rounded-xl border border-slate-200 px-2 py-1 text-sm"
                    value={draft.softSkills.join(", ")}
                    onChange={(e) => updateStringList("softSkills", e.target.value)}
                    placeholder="Comma separated (e.g. Teamwork, Communication)"
                  />
                ) : (
                  <div className="flex flex-wrap gap-2">
                    {target.softSkills.map((s) => (
                      <Badge key={s} tone="gray">
                        {s}
                      </Badge>
                    ))}
                  </div>
                )}
              </div>

              <div>
                <div className="text-xs font-semibold text-slate-500 mb-1">Expertise</div>
                {isEdit ? (
                  <input
                    className="w-full rounded-xl border border-slate-200 px-2 py-1 text-sm"
                    value={draft.expertise.join(", ")}
                    onChange={(e) => updateStringList("expertise", e.target.value)}
                    placeholder="Comma separated (e.g. Nursing, Warehouse)"
                  />
                ) : (
                  <div className="flex flex-wrap gap-2">
                    {target.expertise.map((s) => (
                      <Badge key={s} tone="blue">
                        {s}
                      </Badge>
                    ))}
                  </div>
                )}
              </div>
            </CardContent>
          </Card>

          {/* Languages / Driving licence / Job roles */}
          <Card>
            <CardHeader>
              <CardTitle>Profile details</CardTitle>
            </CardHeader>
            <CardContent className="grid gap-4 md:grid-cols-3 text-sm">
              <div>
                <div className="text-xs font-semibold text-slate-500 mb-1">Languages</div>
                {isEdit ? (
                  <input
                    className="w-full rounded-xl border border-slate-200 px-2 py-1 text-sm"
                    value={draft.languages.join(", ")}
                    onChange={(e) => updateStringList("languages", e.target.value)}
                  />
                ) : (
                  <div className="flex flex-wrap gap-2">
                    {target.languages.map((l) => (
                      <Badge key={l} tone="gray">
                        {l}
                      </Badge>
                    ))}
                  </div>
                )}
              </div>
              <div>
                <div className="text-xs font-semibold text-slate-500 mb-1">Driving licence</div>
                {isEdit ? (
                  <Select
                    multiple
                    className="h-24"
                    value={draft.drivingLicences as any}
                    onChange={(e) =>
                      updateField(
                        "drivingLicences",
                        Array.from(e.target.selectedOptions).map((o) => o.value)
                      )
                    }
                  >
                    <option value="A">A</option>
                    <option value="B">B</option>
                    <option value="C">C</option>
                    <option value="D">D</option>
                  </Select>
                ) : (
                  <div className="flex flex-wrap gap-2">
                    {target.drivingLicences.map((l) => (
                      <Badge key={l} tone="green">
                        {l}
                      </Badge>
                    ))}
                  </div>
                )}
              </div>
              <div>
                <div className="text-xs font-semibold text-slate-500 mb-1">Job roles</div>
                {isEdit ? (
                  <input
                    className="w-full rounded-xl border border-slate-200 px-2 py-1 text-sm"
                    value={draft.jobRoles.join(", ")}
                    onChange={(e) => updateStringList("jobRoles", e.target.value)}
                  />
                ) : (
                  <div className="flex flex-wrap gap-2">
                    {target.jobRoles.map((r) => (
                      <Badge key={r} tone="blue">
                        {r}
                      </Badge>
                    ))}
                  </div>
                )}
              </div>
            </CardContent>
          </Card>

          {/* Experiences */}
          <Card>
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle>Experience</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3 text-sm">
              {isEdit ? (
                <>
                  {draft.experiences.map((exp) => (
                    <div
                      key={exp.id}
                      className="grid gap-2 rounded-2xl border border-slate-200 p-3 md:grid-cols-[2fr_2fr_1fr_1fr_auto] items-start"
                    >
                      <input
                        className="rounded-xl border border-slate-200 px-2 py-1 text-sm"
                        value={exp.company}
                        onChange={(e) => updateExperience(exp.id, "company", e.target.value)}
                        placeholder="Company"
                      />
                      <input
                        className="rounded-xl border border-slate-200 px-2 py-1 text-sm"
                        value={exp.role}
                        onChange={(e) => updateExperience(exp.id, "role", e.target.value)}
                        placeholder="Role"
                      />
                      <input
                        className="rounded-xl border border-slate-200 px-2 py-1 text-xs"
                        value={exp.from}
                        onChange={(e) => updateExperience(exp.id, "from", e.target.value)}
                        placeholder="From (e.g. Jan 2022)"
                      />
                      <input
                        className="rounded-xl border border-slate-200 px-2 py-1 text-xs"
                        value={exp.to ?? ""}
                        onChange={(e) => updateExperience(exp.id, "to", e.target.value)}
                        placeholder="To (or leave blank for current)"
                      />
                      <button
                        type="button"
                        className="text-xs font-semibold text-rose-600 hover:text-rose-700"
                        onClick={() => removeExperience(exp.id)}
                      >
                        Remove
                      </button>
                    </div>
                  ))}
                  <button
                    type="button"
                    className="text-xs font-semibold text-slate-600 hover:text-slate-900"
                    onClick={addExperience}
                  >
                    + Add experience
                  </button>
                </>
              ) : (
                <div className="space-y-3">
                  {target.experiences.map((exp) => (
                    <div key={exp.id} className="flex items-baseline justify-between gap-3">
                      <div>
                        <div className="font-semibold">{exp.role}</div>
                        <div className="text-xs text-slate-500">{exp.company}</div>
                      </div>
                      <div className="text-xs text-slate-400">
                        {exp.from} – {exp.to ?? "Present"}
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </CardContent>
          </Card>

          {/* Documents, references, education & certificates */}
          <div className="grid gap-4 md:grid-cols-2">
            <Card>
              <CardHeader>
                <CardTitle>Linked documents</CardTitle>
              </CardHeader>
              <CardContent className="space-y-2 text-sm">
                {isEdit ? (
                  <>
                    {draft.documents.map((doc) => (
                      <div
                        key={doc.id}
                        className="grid gap-2 rounded-2xl border border-slate-200 p-3 md:grid-cols-[2fr_1fr_2fr_auto] items-start"
                      >
                        <input
                          className="rounded-xl border border-slate-200 px-2 py-1 text-sm"
                          value={doc.name}
                          onChange={(e) => updateDocument(doc.id, "name", e.target.value)}
                          placeholder="Document name"
                        />
                        <select
                          className="rounded-xl border border-slate-200 px-2 py-1 text-xs bg-white"
                          value={doc.type}
                          onChange={(e) => updateDocument(doc.id, "type", e.target.value as any)}
                        >
                          <option value="cv">CV</option>
                          <option value="certificate">Certificate</option>
                          <option value="reference">Reference</option>
                          <option value="id">ID</option>
                        </select>
                        <input
                          className="rounded-xl border border-slate-200 px-2 py-1 text-xs"
                          value={doc.url}
                          onChange={(e) => updateDocument(doc.id, "url", e.target.value)}
                          placeholder="Document URL"
                        />
                        <button
                          type="button"
                          className="text-xs font-semibold text-rose-600 hover:text-rose-700"
                          onClick={() => removeDocument(doc.id)}
                        >
                          Remove
                        </button>
                      </div>
                    ))}
                    <button
                      type="button"
                      className="text-xs font-semibold text-slate-600 hover:text-slate-900"
                      onClick={addDocument}
                    >
                      + Add document
                    </button>
                  </>
                ) : (
                  target.documents.map((doc) => (
                    <button
                      key={doc.id}
                      className="w-full flex items-center justify-between rounded-2xl border border-slate-200 px-3 py-2 text-left hover:bg-slate-50"
                      type="button"
                      onClick={() => window.open(doc.url, "_blank")}
                    >
                      <div className="flex items-center gap-2">
                        <FileText size={14} className="text-slate-400" />
                        <div>
                          <div className="font-semibold text-xs">{doc.name}</div>
                          <div className="text-[11px] text-slate-400 uppercase">
                            {doc.type}
                          </div>
                        </div>
                      </div>
                      <span className="text-[11px] text-slate-500">Open</span>
                    </button>
                  ))
                )}
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>Education & certificates</CardTitle>
              </CardHeader>
              <CardContent className="space-y-3 text-sm">
                <div>
                  <div className="text-xs font-semibold text-slate-500 mb-1">
                    Education
                  </div>
                  {isEdit ? (
                    <textarea
                      className="w-full rounded-2xl border border-slate-200 px-3 py-2 text-sm min-h-[60px]"
                      value={draft.education}
                      onChange={(e) => updateField("education", e.target.value)}
                    />
                  ) : (
                    <p className="text-slate-700">{target.education}</p>
                  )}
                </div>
                <div>
                  <div className="text-xs font-semibold text-slate-500 mb-1">
                    Certificates
                  </div>
                  {isEdit ? (
                    <input
                      className="w-full rounded-xl border border-slate-200 px-2 py-1 text-sm"
                      value={draft.certificates.join(", ")}
                      onChange={(e) => updateStringList("certificates", e.target.value)}
                    />
                  ) : (
                    <ul className="list-disc pl-4 space-y-1 text-slate-700">
                      {target.certificates.map((c) => (
                        <li key={c}>{c}</li>
                      ))}
                    </ul>
                  )}
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>

      {/* Profile strength */}
      <ProfileStrength profile={target} completeness={completeness} />

      {/* Complete your profile – attributes that affect strength */}
      <div className="space-y-4">
        <h2 className="text-lg font-semibold text-slate-800">Complete your profile</h2>
        <p className="text-sm text-slate-600">
          Add these details to improve your profile strength and get more opportunities.
        </p>
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          <ProfileSectionCard
            title="Birthdate"
            description="Adding your birthdate helps verify your identity."
            icon={Calendar}
            actionLabel={completeness.birthdate ? "Edit" : "Add birthdate"}
            onAction={() => {}}
          />
          <ProfileSectionCard
            title="Work experience"
            description="Adding work experience increases your chances of getting selected."
            icon={Briefcase}
            actionLabel={profile.experiences.length > 0 ? "Manage experience" : "+ Add experience"}
            onAction={() => startEdit()}
          />
          <ProfileSectionCard
            title="Verify employment"
            description="Verify your employment history with documents."
            icon={BadgeCheck}
            actionLabel={completeness.employmentVerified ? "Verified" : "Verify employment"}
            onAction={() => setCompleteness((c) => ({ ...c, employmentVerified: !c.employmentVerified }))}
          />
          <ProfileSectionCard
            title="Complete quiz"
            description="Show you have the know-how for applied positions."
            icon={HelpCircle}
            actionLabel={completeness.quizCompleted ? "Completed" : "Start quiz"}
            onAction={() => setCompleteness((c) => ({ ...c, quizCompleted: !c.quizCompleted }))}
          />
          <ProfileSectionCard
            title="Skill assessment"
            description="Complete assessments to showcase your skills."
            icon={ClipboardCheck}
            actionLabel="Add skills"
            onAction={() => {}}
            badge={completeness.skillAssessmentsCompleted > 0 ? `Completed: ${completeness.skillAssessmentsCompleted}` : undefined}
          />
          <ProfileSectionCard
            title="Add language"
            description="Languages you speak improve matching with jobs."
            icon={Languages}
            actionLabel={profile.languages.length > 0 ? "Edit languages" : "+ Add language"}
            onAction={() => startEdit()}
          />
          <ProfileSectionCard
            title="Add education"
            description="Adding education helps make your profile more complete."
            icon={GraduationCap}
            actionLabel={profile.education ? "Edit education" : "+ Add education"}
            onAction={() => startEdit()}
          />
        </div>
      </div>

      {/* Extra sections (Instawork-style) */}
      <div className="space-y-4">
        <h2 className="text-lg font-semibold text-slate-800">More</h2>
        <div className="space-y-4">
          <ProfileSectionCard
            title="My positions"
            description="Explore, manage and get approved for positions that match your skills."
            icon={Briefcase}
            actionLabel="Manage positions"
            onAction={() => setIsProfilePickerOpen(true)}
            items={[
              { label: "Available", count: completeness.positionsAvailable },
              { label: "Eligible", count: completeness.positionsEligible },
              { label: "Need further review", count: completeness.positionsNeedReview },
              { label: "Not eligible", count: completeness.positionsNotEligible },
            ]}
          />
          <ProfileSectionCard
            title="My skills"
            description="Showcase your skills to connect with the right opportunities."
            icon={ClipboardCheck}
            tag="NEW"
            actionLabel="Add skills"
            onAction={() => {}}
            items={[{ label: "Completed assessments", count: completeness.skillAssessmentsCompleted }]}
          />
          <ProfileSectionCard
            title="Work experience"
            description="Adding work experience increases your chances of getting selected."
            icon={Briefcase}
            actionLabel="+ Add experience"
            onAction={() => startEdit()}
          />
          <ProfileSectionCard
            title="Businesses that favorited you"
            description="Doing a great job encourages businesses to request you for future shifts."
            icon={Users}
            actionLabel="View businesses"
            onAction={() => {}}
          />
          <ProfileSectionCard
            title="Import resume"
            description="Upload your resume so we can give partners a fuller picture of your experience."
            icon={FileUp}
            actionLabel={completeness.resumeImported ? "Update resume" : "Import resume"}
            onAction={() => setCompleteness((c) => ({ ...c, resumeImported: !c.resumeImported }))}
          />
          <ProfileSectionCard
            title="Contributions"
            description="Help us bring you more shifts by sharing some quick info."
            icon={Flame}
            secondaryActionLabel="Contribute now"
            onSecondaryAction={() => {}}
          />
          <ProfileSectionCard
            title="My network"
            description="Build a network. Get more opportunities."
            icon={Users}
            secondaryActionLabel="Learn more"
            onSecondaryAction={() => {}}
          />
          <ProfileSectionCard
            title="Training"
            description="Learn new skills and showcase your expertise."
            icon={GraduationCap}
            actionLabel="View training"
            onAction={() => {}}
            badge="18 more available"
          />
          <ProfileSectionCard
            title="Achievements"
            description="Stand out as a Professional with badges that you can earn through work."
            icon={Award}
            actionLabel="Start earning badges"
            onAction={() => {}}
          />
          <ProfileSectionCard
            title="Quiz"
            description="Do you have the know-how to work for applied positions?"
            icon={HelpCircle}
            actionLabel={completeness.quizCompleted ? "View results" : "Start quiz"}
            onAction={() => setCompleteness((c) => ({ ...c, quizCompleted: !c.quizCompleted }))}
          />
          <ProfileSectionCard
            title="Public profile"
            description="Share your profile with businesses so they can request you for their shifts."
            icon={Share2}
            actionLabel="Share"
            onAction={() => {}}
            secondaryActionLabel="Preview"
            onSecondaryAction={() => window.open(completeness.publicProfileUrl ?? "#", "_blank")}
          />
          <ProfileSectionCard
            title="Certificates"
            description="Add and manage your certificates for required roles."
            icon={FileText}
            actionLabel="View all"
            onAction={() => {}}
          />
          <ProfileSectionCard
            title="Attire"
            description="Verify that you have the right attire by uploading your full-size photo wearing them."
            icon={Shirt}
            actionLabel={completeness.attireVerified ? "Verified" : "Verify attire"}
            onAction={() => setCompleteness((c) => ({ ...c, attireVerified: !c.attireVerified }))}
          />
          <ProfileSectionCard
            title="Education"
            description="Adding education helps make your profile more complete."
            icon={BookOpen}
            actionLabel="+ Add education"
            onAction={() => startEdit()}
          />
          <ProfileSectionCard
            title="W-2 onboarding"
            description="Unlock more shifts by completing your paperwork for W-2 shifts."
            icon={FileCheck}
            actionLabel={completeness.w2OnboardingCompleted ? "Completed" : "Start onboarding"}
            onAction={() => setCompleteness((c) => ({ ...c, w2OnboardingCompleted: !c.w2OnboardingCompleted }))}
          />
        </div>
      </div>

      {isProfilePickerOpen && (
        <div className="fixed inset-0 z-40 flex items-center justify-center bg-slate-900/40">
          <div className="w-full max-w-md">
            <Card>
              <CardHeader className="flex flex-row items-center justify-between">
                <CardTitle>Select job profile</CardTitle>
                <Button variant="ghost" onClick={() => setIsProfilePickerOpen(false)}>
                  <X size={16} />
                </Button>
              </CardHeader>
              <CardContent className="space-y-3 text-sm">
                {jobProfilesQuery.isLoading && (
                  <div className="text-slate-500">Loading profiles…</div>
                )}
                {!jobProfilesQuery.isLoading && !jobProfiles.length && (
                  <div className="text-slate-500">No job profiles found for this worker.</div>
                )}
                {jobProfiles.map((p) => (
                  <button
                    key={p.id}
                    type="button"
                    className={`w-full flex items-center justify-between rounded-2xl border px-3 py-2 text-left ${
                      p.id === currentJobProfile?.id
                        ? "border-slate-900 bg-slate-900 text-white"
                        : "border-slate-200 bg-white hover:bg-slate-50"
                    }`}
                    onClick={() => {
                      setSelectedJobProfileId(p.id);
                      setProfile((prev) => ({ ...prev, approvalStatus: p.approvalStatus }));
                      setDraft((prev) => ({ ...prev, approvalStatus: p.approvalStatus }));
                      setIsProfilePickerOpen(false);
                    }}
                  >
                    <div>
                      <div className="text-sm font-semibold">{p.label}</div>
                      <div className="text-xs text-slate-500">
                        {p.approvalStatus === "approved" ? "Approved" : "Pending admin approval"}
                      </div>
                    </div>
                    <Badge tone={p.approvalStatus === "approved" ? "green" : "yellow"}>
                      {p.approvalStatus === "approved" ? "Approved" : "Pending"}
                    </Badge>
                  </button>
                ))}
              </CardContent>
            </Card>
          </div>
        </div>
      )}
    </div>
  );
}