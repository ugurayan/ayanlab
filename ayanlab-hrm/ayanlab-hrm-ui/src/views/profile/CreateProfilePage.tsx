import React, { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "../../ui/components/Card";
import { Badge } from "../../ui/components/Badge";
import { Button } from "../../ui/components/Button";
import { Select } from "../../ui/components/Select";
import { Profile } from "../../types/profile";
import { Mail, Phone, MapPin, Star, Edit2, Check, X, FileText } from "lucide-react";

const emptyProfile: Profile = {
  id: "new_profile",
  avatarUrl: "https://i.pravatar.cc/120?u=new-profile",
  displayName: "",
  firstName: "",
  lastName: "",
  rating: 0,
  ratingCount: 0,
  email: "",
  phone: "",
  location: "",
  about: "",
  softSkills: [],
  expertise: [],
  languages: [],
  drivingLicences: [],
  jobRoles: [],
  experiences: [],
  documents: [],
  education: "",
  certificates: [],
  approvalStatus: "pending_admin"
};

export function CreateProfilePage() {
  const [profile, setProfile] = useState<Profile>(emptyProfile);
  const [draft, setDraft] = useState<Profile>(emptyProfile);
  const [isEdit, setIsEdit] = useState(true);

  const startEdit = () => {
    setDraft(profile);
    setIsEdit(true);
  };

  const cancelEdit = () => {
    setDraft(profile);
    setIsEdit(false);
  };

  const saveEdit = () => {
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

  const target = isEdit ? draft : profile;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <div className="text-2xl font-bold">Create profile</div>
          <div className="text-sm text-slate-500">
            Enter all details to create a new worker profile.
          </div>
        </div>
        <div className="flex items-center gap-3">
          {profile.approvalStatus === "pending_admin" && !isEdit && (
            <Badge tone="yellow">Changes pending admin approval</Badge>
          )}
          {!isEdit ? (
            <Button variant="secondary" onClick={startEdit}>
              <Edit2 size={16} className="mr-1" />
              Edit
            </Button>
          ) : (
            <div className="flex gap-2">
              <Button variant="secondary" onClick={cancelEdit}>
                <X size={16} className="mr-1" />
                Cancel
              </Button>
              <Button onClick={saveEdit}>
                <Check size={16} className="mr-1" />
                Save profile
              </Button>
            </div>
          )}
        </div>
      </div>

      <div className="grid gap-6 lg:grid-cols-[280px_1fr]">
        <div className="space-y-4">
          <Card>
            <CardContent className="p-5 flex flex-col items-center text-center gap-4">
              <img
                src={target.avatarUrl}
                alt={target.displayName || "New profile"}
                className="h-24 w-24 rounded-full object-cover border border-slate-200"
              />
              <div>
                {isEdit ? (
                  <>
                    <input
                      className="w-full rounded-xl border border-slate-200 px-3 py-1 text-sm mb-1"
                      value={draft.displayName}
                      onChange={(e) => updateField("displayName", e.target.value)}
                      placeholder="Display name"
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
                <input
                  className="w-full rounded-xl border border-slate-200 px-2 py-1 text-sm"
                  value={draft.email}
                  onChange={(e) => updateField("email", e.target.value)}
                  placeholder="Email address"
                />
              </div>
              <div className="flex items-center gap-2">
                <Phone size={14} className="text-slate-400" />
                <input
                  className="w-full rounded-xl border border-slate-200 px-2 py-1 text-sm"
                  value={draft.phone}
                  onChange={(e) => updateField("phone", e.target.value)}
                  placeholder="Phone number"
                />
              </div>
              <div className="flex items-center gap-2">
                <MapPin size={14} className="text-slate-400" />
                <input
                  className="w-full rounded-xl border border-slate-200 px-2 py-1 text-sm"
                  value={draft.location}
                  onChange={(e) => updateField("location", e.target.value)}
                  placeholder="Location"
                />
              </div>
            </CardContent>
          </Card>
        </div>

        <div className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>About</CardTitle>
            </CardHeader>
            <CardContent>
              <textarea
                className="w-full rounded-2xl border border-slate-200 px-3 py-2 text-sm min-h-[100px]"
                value={draft.about}
                onChange={(e) => updateField("about", e.target.value)}
                placeholder="Short bio, experience and preferences"
              />
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Skills & Expertise</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4 text-sm">
              <div>
                <div className="text-xs font-semibold text-slate-500 mb-1">Soft skills</div>
                <input
                  className="w-full rounded-xl border border-slate-200 px-2 py-1 text-sm"
                  value={draft.softSkills.join(", ")}
                  onChange={(e) => updateStringList("softSkills", e.target.value)}
                  placeholder="Comma separated (e.g. Teamwork, Communication)"
                />
              </div>

              <div>
                <div className="text-xs font-semibold text-slate-500 mb-1">Expertise</div>
                <input
                  className="w-full rounded-xl border border-slate-200 px-2 py-1 text-sm"
                  value={draft.expertise.join(", ")}
                  onChange={(e) => updateStringList("expertise", e.target.value)}
                  placeholder="Comma separated (e.g. Nursing, Warehouse)"
                />
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Profile details</CardTitle>
            </CardHeader>
            <CardContent className="grid gap-4 md:grid-cols-3 text-sm">
              <div>
                <div className="text-xs font-semibold text-slate-500 mb-1">Languages</div>
                <input
                  className="w-full rounded-xl border border-slate-200 px-2 py-1 text-sm"
                  value={draft.languages.join(", ")}
                  onChange={(e) => updateStringList("languages", e.target.value)}
                  placeholder="Comma separated (e.g. English, German)"
                />
              </div>
              <div>
                <div className="text-xs font-semibold text-slate-500 mb-1">Driving licence</div>
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
              </div>
              <div>
                <div className="text-xs font-semibold text-slate-500 mb-1">Job roles</div>
                <input
                  className="w-full rounded-xl border border-slate-200 px-2 py-1 text-sm"
                  value={draft.jobRoles.join(", ")}
                  onChange={(e) => updateStringList("jobRoles", e.target.value)}
                  placeholder="Comma separated (e.g. Registered Nurse, Warehouse Operative)"
                />
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Linked documents</CardTitle>
            </CardHeader>
            <CardContent className="space-y-2 text-sm">
              {draft.documents.length === 0 && (
                <div className="text-slate-500 text-xs mb-2">No documents added yet.</div>
              )}
              {draft.documents.map((doc) => (
                <div
                  key={doc.id}
                  className="grid gap-2 rounded-2xl border border-slate-200 p-3 md:grid-cols-[2fr_1fr_2fr_auto] items-start"
                >
                  <input
                    className="rounded-xl border border-slate-200 px-2 py-1 text-sm"
                    value={doc.name}
                    onChange={(e) =>
                      setDraft((prev) => ({
                        ...prev,
                        documents: prev.documents.map((d) =>
                          d.id === doc.id ? { ...d, name: e.target.value } : d
                        )
                      }))
                    }
                    placeholder="Document name"
                  />
                  <select
                    className="rounded-xl border border-slate-200 px-2 py-1 text-xs bg-white"
                    value={doc.type}
                    onChange={(e) =>
                      setDraft((prev) => ({
                        ...prev,
                        documents: prev.documents.map((d) =>
                          d.id === doc.id ? { ...d, type: e.target.value as any } : d
                        )
                      }))
                    }
                  >
                    <option value="cv">CV</option>
                    <option value="certificate">Certificate</option>
                    <option value="reference">Reference</option>
                    <option value="id">ID</option>
                  </select>
                  <input
                    className="rounded-xl border border-slate-200 px-2 py-1 text-xs"
                    value={doc.url}
                    onChange={(e) =>
                      setDraft((prev) => ({
                        ...prev,
                        documents: prev.documents.map((d) =>
                          d.id === doc.id ? { ...d, url: e.target.value } : d
                        )
                      }))
                    }
                    placeholder="Document URL"
                  />
                  <button
                    type="button"
                    className="text-xs font-semibold text-rose-600 hover:text-rose-700"
                    onClick={() =>
                      setDraft((prev) => ({
                        ...prev,
                        documents: prev.documents.filter((d) => d.id !== doc.id)
                      }))
                    }
                  >
                    Remove
                  </button>
                </div>
              ))}
              <button
                type="button"
                className="text-xs font-semibold text-slate-600 hover:text-slate-900"
                onClick={() =>
                  setDraft((prev) => ({
                    ...prev,
                    documents: [
                      ...prev.documents,
                      { id: `doc_${Date.now()}`, name: "", type: "cv", url: "" }
                    ]
                  }))
                }
              >
                + Add document
              </button>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Education & certificates</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3 text-sm">
              <div>
                <div className="text-xs font-semibold text-slate-500 mb-1">Education</div>
                <textarea
                  className="w-full rounded-2xl border border-slate-200 px-3 py-2 text-sm min-h-[60px]"
                  value={draft.education}
                  onChange={(e) => updateField("education", e.target.value)}
                />
              </div>
              <div>
                <div className="text-xs font-semibold text-slate-500 mb-1">Certificates</div>
                <input
                  className="w-full rounded-xl border border-slate-200 px-2 py-1 text-sm"
                  value={draft.certificates.join(", ")}
                  onChange={(e) => updateStringList("certificates", e.target.value)}
                  placeholder="Comma separated (e.g. ACLS, BLS)"
                />
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
