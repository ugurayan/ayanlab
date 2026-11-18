import React, { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "../../ui/components/Card";
import { Button } from "../../ui/components/Button";
import { Input } from "../../ui/components/Input";
import type { AccountSettings as AccountSettingsModel, LanguageLevel } from "../../types/settings";
import { useTranslations } from "../../state/i18n";
import { SETTINGS_TEXTS, FOOTER_TEXTS, ACCOUNT_TEXTS, SECURITY_TEXTS } from "./i18n";
import { useAuth } from "../../state/auth";

const sections = ["account", "security", "plan", "notifications", "team", "app", "bank", "cards", "org"] as const;
type SectionId = typeof sections[number];

export function SettingsPage() {
  const [active, setActive] = useState<SectionId>("account");
  const copy = useTranslations(SETTINGS_TEXTS);
  const { user } = useAuth();

  const isOrgAdmin = user?.roles.includes("ORG_ADMIN") || user?.roles.includes("PLATFORM_ADMIN");

  return (
    <div className="grid gap-6 lg:grid-cols-[260px_1fr]">
      <aside className="space-y-4">
        <div>
          <div className="text-2xl font-bold">{copy.hero.title}</div>
          <div className="text-sm text-slate-500">{copy.hero.subtitle}</div>
        </div>

        <nav className="mt-2 flex flex-col gap-1">
          {sections.map((sectionId) => {
            if (sectionId === "org" && !isOrgAdmin) return null;
            return (
            <button
              key={sectionId}
              type="button"
              onClick={() => setActive(sectionId)}
              className={`flex flex-col items-start rounded-2xl px-3 py-2 text-left text-sm border ${
                active === sectionId
                  ? "border-slate-900 bg-slate-900 text-white"
                  : "border-transparent text-slate-700 hover:bg-slate-50"
              }`}
            >
              <span className="font-semibold">{copy.sections[sectionId].label}</span>
              <span className="text-xs text-slate-400 line-clamp-2">{copy.sections[sectionId].description}</span>
            </button>
          );})}
        </nav>
      </aside>

      <section className="space-y-4">
        {active === "account" && <AccountSettings />}
        {active === "security" && <SecuritySettings />}
        {active === "plan" && <PlanSettings />}
        {active === "notifications" && <NotificationSettings />}
        {active === "team" && <TeamSettings />}
        {active === "app" && <AppSettings />}
        {active === "bank" && <BankAccountsSettings />}
        {active === "cards" && <CreditCardsSettings />}
        {active === "org" && isOrgAdmin && <OrganizationSettings />}
      </section>
    </div>
  );
}

function FormFooter() {
  const copy = useTranslations(FOOTER_TEXTS);
  return (
    <div className="mt-6 flex justify-end gap-2">
      <Button variant="secondary" type="button">
        {copy.cancel}
      </Button>
      <Button type="button">{copy.save}</Button>
    </div>
  );
}

const initialAccountSettings: AccountSettingsModel = {
  profileName: "Brian Hughes",
  company: "XYZ Software",
  title: "Senior Frontend Developer",
  notes:
    "Hey! This is Brian; husband, father and gamer. I'm mostly passionate about bleeding edge tech and chocolate!",
  email: "hughes.brian@mail.com",
  phone: "121-490-33-12",
  country: "United States",
  language: "English",
  languages: [
    { code: "en", level: "native" },
    { code: "de", level: "intermediate" }
  ],
  isEmailVerified: true,
  isPhoneVerified: true,
  isDocumentsVerified: true,
  isCertificatesVerified: false,
  workPermit: "B-permit (Switzerland)",
  drivingLicense: "Category B",
  itin: "123-45-6789",
  employerId: "EMP-123456",
  ssn: "XXX-XX-1234",
  approvalStatus: "approved"
};

function AccountSettings() {
  const [account, setAccount] = useState<AccountSettingsModel>(initialAccountSettings);
  const [draft, setDraft] = useState<AccountSettingsModel>(initialAccountSettings);
  const [isEdit, setIsEdit] = useState(false);
  const [isConfirmOpen, setIsConfirmOpen] = useState(false);
  const copy = useTranslations(ACCOUNT_TEXTS);

  const updateField = <K extends keyof AccountSettingsModel>(key: K, value: AccountSettingsModel[K]) => {
    setDraft((prev) => ({ ...prev, [key]: value }));
  };

  const startEdit = () => {
    setDraft(account);
    setIsEdit(true);
  };

  const cancelEdit = () => {
    setDraft(account);
    setIsEdit(false);
  };

  const requestSave = () => {
    setIsConfirmOpen(true);
  };

  const confirmSave = () => {
    setAccount({ 
      ...draft, 
      approvalStatus: "pending_admin",
      isEmailVerified: false,
      isPhoneVerified: false,
      isDocumentsVerified: false,
      isCertificatesVerified: false
    });
    setIsEdit(false);
    setIsConfirmOpen(false);
  };

  const isPending = account.approvalStatus === "pending_admin";

  return (
    <>
      <Card>
        <CardHeader className="flex items-center justify-between">
          <div>
            <CardTitle>{copy.title}</CardTitle>
            {isPending && (
              <div className="mt-1 text-xs text-amber-700">
                {copy.pending}
              </div>
            )}
          </div>
          <div className="flex items-center gap-2">
            {isPending && (
              <span className="rounded-full border border-amber-200 bg-amber-50 px-2 py-0.5 text-[11px] font-semibold text-amber-700">
                {copy.chip}
              </span>
            )}
            {!isEdit ? (
              <Button variant="secondary" type="button" onClick={startEdit}>
                {copy.actions.edit}
              </Button>
            ) : (
              <div className="flex gap-2">
                <Button variant="secondary" type="button" onClick={cancelEdit}>
                  {copy.actions.cancel}
                </Button>
                <Button type="button" onClick={requestSave}>
                    {copy.actions.save}
                </Button>
              </div>
            )}
          </div>
        </CardHeader>
        <CardContent className="space-y-6">
          <div>
            <div className="text-sm font-semibold mb-2">{copy.profile.title}</div>
            <div className="grid gap-4 md:grid-cols-2">
              <div className="space-y-1">
                <label className="text-xs font-semibold text-slate-600">{copy.profile.name}</label>
                <Input
                  placeholder={copy.profile.namePlaceholder}
                  value={isEdit ? draft.profileName : account.profileName}
                  onChange={(e) => isEdit && updateField("profileName", e.target.value)}
                  readOnly={!isEdit}
                />
              </div>
              <div className="space-y-1">
                <label className="text-xs font-semibold text-slate-600">{copy.profile.company}</label>
                <Input
                  placeholder={copy.profile.companyPlaceholder}
                  value={isEdit ? draft.company : account.company}
                  onChange={(e) => isEdit && updateField("company", e.target.value)}
                  readOnly={!isEdit}
                />
              </div>
              <div className="space-y-1 md:col-span-2">
                <label className="text-xs font-semibold text-slate-600">{copy.profile.titleLabel}</label>
                <Input
                  placeholder={copy.profile.titlePlaceholder}
                  value={isEdit ? draft.title : account.title}
                  onChange={(e) => isEdit && updateField("title", e.target.value)}
                  readOnly={!isEdit}
                />
              </div>
              <div className="space-y-1 md:col-span-2">
                <label className="text-xs font-semibold text-slate-600">{copy.profile.notes}</label>
                <textarea
                  className="w-full rounded-2xl border border-slate-200 px-3 py-2 text-sm min-h-[100px]"
                  value={isEdit ? draft.notes : account.notes}
                  onChange={(e) => isEdit && updateField("notes", e.target.value)}
                  readOnly={!isEdit}
                />
                <p className="text-xs text-slate-400">{copy.profile.notesHint}</p>
              </div>
            </div>
          </div>

          <div>
            <div className="text-sm font-semibold mb-2">{copy.personal.title}</div>
            <div className="grid gap-4 md:grid-cols-2">
              <div className="space-y-1">
                <label className="text-xs font-semibold text-slate-600">{copy.personal.email}</label>
                <div className="flex items-center gap-2">
                  <Input
                    placeholder={copy.personal.emailPlaceholder}
                    value={isEdit ? draft.email : account.email}
                    onChange={(e) => isEdit && updateField("email", e.target.value)}
                    readOnly={!isEdit}
                  />
                  {!account.isEmailVerified && (
                    <Button
                      type="button"
                      variant="secondary"
                      size="sm"
                      onClick={() =>
                        setAccount((prev) => ({ ...prev, isEmailVerified: true }))
                      }
                    >
                      Verify
                    </Button>
                  )}
                  {account.isEmailVerified && (
                    <span className="text-[11px] font-semibold text-emerald-600">Verified</span>
                  )}
                </div>
              </div>
              <div className="space-y-1">
                <label className="text-xs font-semibold text-slate-600">{copy.personal.phone}</label>
                <div className="flex items-center gap-2">
                  <Input
                    placeholder={copy.personal.phonePlaceholder}
                    value={isEdit ? draft.phone : account.phone}
                    onChange={(e) => isEdit && updateField("phone", e.target.value)}
                    readOnly={!isEdit}
                  />
                  {!account.isPhoneVerified && (
                    <Button
                      type="button"
                      variant="secondary"
                      size="sm"
                      onClick={() =>
                        setAccount((prev) => ({ ...prev, isPhoneVerified: true }))
                      }
                    >
                      Verify
                    </Button>
                  )}
                  {account.isPhoneVerified && (
                    <span className="text-[11px] font-semibold text-emerald-600">Verified</span>
                  )}
                </div>
              </div>
              <div className="space-y-1">
                <label className="text-xs font-semibold text-slate-600">{copy.personal.country}</label>
                <Input
                  placeholder={copy.personal.countryPlaceholder}
                  value={isEdit ? draft.country : account.country}
                  onChange={(e) => isEdit && updateField("country", e.target.value)}
                  readOnly={!isEdit}
                />
              </div>
              <div className="space-y-1">
                <label className="text-xs font-semibold text-slate-600">{copy.personal.language}</label>
                <Input
                  placeholder={copy.personal.languagePlaceholder}
                  value={isEdit ? draft.language : account.language}
                  onChange={(e) => isEdit && updateField("language", e.target.value)}
                  readOnly={!isEdit}
                />
              </div>
            </div>
          </div>

          {/* Multi-language skills */}
          <div>
            <div className="text-sm font-semibold mb-2">Languages & Levels</div>
            <div className="space-y-2">
              {(isEdit ? draft.languages : account.languages).map((item, idx) => (
                <div key={idx} className="grid gap-2 md:grid-cols-[2fr_1.5fr]">
                  <Input
                    placeholder="Language (e.g. English)"
                    value={item.code}
                    onChange={(e) => {
                      if (!isEdit) return;
                      const next = [...draft.languages];
                      next[idx] = { ...next[idx], code: e.target.value };
                      updateField("languages", next as unknown as AccountSettingsModel["languages"]);
                    }}
                    readOnly={!isEdit}
                  />
                  <select
                    className="rounded-2xl border border-slate-200 px-3 py-2 text-sm bg-white"
                    value={item.level}
                    onChange={(e) => {
                      if (!isEdit) return;
                      const next = [...draft.languages];
                      next[idx] = { ...next[idx], level: e.target.value as LanguageLevel["level"] };
                      updateField("languages", next as unknown as AccountSettingsModel["languages"]);
                    }}
                    disabled={!isEdit}
                  >
                    <option value="basic">Basic</option>
                    <option value="intermediate">Intermediate</option>
                    <option value="advanced">Advanced</option>
                    <option value="native">Native</option>
                  </select>
                </div>
              ))}
              {isEdit && (
                <Button
                  type="button"
                  variant="secondary"
                  size="sm"
                  onClick={() =>
                    updateField("languages", [
                      ...(draft.languages ?? []),
                      { code: "", level: "basic" }
                    ] as unknown as AccountSettingsModel["languages"])
                  }
                >
                  + Add language
                </Button>
              )}
            </div>
          </div>

          {/* Additional documents & IDs */}
          <div>
            <div className="text-sm font-semibold mb-2">Work & Legal</div>
            <div className="grid gap-4 md:grid-cols-2">
              <div className="space-y-1">
                <label className="text-xs font-semibold text-slate-600">Work permit</label>
                <Input
                  placeholder="e.g. B-permit, EU citizen"
                  value={isEdit ? (draft.workPermit ?? "") : (account.workPermit ?? "")}
                  onChange={(e) => isEdit && updateField("workPermit", e.target.value)}
                  readOnly={!isEdit}
                />
              </div>
              <div className="space-y-1">
                <label className="text-xs font-semibold text-slate-600">Driving licence</label>
                <Input
                  placeholder="e.g. Category B"
                  value={isEdit ? (draft.drivingLicense ?? "") : (account.drivingLicense ?? "")}
                  onChange={(e) => isEdit && updateField("drivingLicense", e.target.value)}
                  readOnly={!isEdit}
                />
              </div>
              <SensitiveField
                label="ITIN"
                placeholder="Mock ITIN"
                value={isEdit ? (draft.itin ?? "") : (account.itin ?? "")}
                onChange={(v) => isEdit && updateField("itin", v as any)}
                readOnly={!isEdit}
              />
              <SensitiveField
                label="Employer ID"
                placeholder="Mock Employer ID"
                value={isEdit ? (draft.employerId ?? "") : (account.employerId ?? "")}
                onChange={(v) => isEdit && updateField("employerId", v as any)}
                readOnly={!isEdit}
              />
              <SensitiveField
                label="SSN"
                placeholder="Mock SSN"
                value={isEdit ? (draft.ssn ?? "") : (account.ssn ?? "")}
                onChange={(v) => isEdit && updateField("ssn", v as any)}
                readOnly={!isEdit}
              />
            </div>
          </div>
        </CardContent>
      </Card>

      {isConfirmOpen && (
        <div className="fixed inset-0 z-40 flex items-center justify-center bg-slate-900/40">
          <div className="w-full max-w-md">
            <Card>
              <CardHeader>
                <CardTitle>{copy.modal.title}</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4 text-sm">
                <p className="text-slate-600">
                  {copy.modal.body}
                </p>
                <div className="flex justify-end gap-2">
                  <Button variant="secondary" type="button" onClick={() => setIsConfirmOpen(false)}>
                    {copy.actions.cancel}
                  </Button>
                  <Button type="button" onClick={confirmSave}>
                    {copy.modal.submit}
                  </Button>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      )}
    </>
  );
}

type SensitiveFieldProps = {
  label: string;
  placeholder?: string;
  value: string;
  onChange: (value: string) => void;
  readOnly?: boolean;
};

function SensitiveField({ label, placeholder, value, onChange, readOnly }: SensitiveFieldProps) {
  const [visible, setVisible] = useState(false);
  return (
    <div className="space-y-1">
      <label className="text-xs font-semibold text-slate-600">{label}</label>
      <div className="flex items-center gap-2">
        <Input
          type={visible ? "text" : "password"}
          placeholder={placeholder}
          value={value}
          onChange={(e) => onChange(e.target.value)}
          readOnly={readOnly}
        />
        <button
          type="button"
          className="text-xs text-slate-500 hover:text-slate-800"
          onClick={() => setVisible((v) => !v)}
        >
          {visible ? "Hide" : "Show"}
        </button>
      </div>
    </div>
  );
}

function BankAccountsSettings() {
  const mockAccounts = [
    { id: 1, bankName: "Mock Bank AG", iban: "CH93 0076 2011 6238 5295 7", currency: "CHF" },
    { id: 2, bankName: "Coople Bank", iban: "CH12 0000 0000 1234 5678 9", currency: "EUR" }
  ];

  return (
    <Card>
      <CardHeader>
        <CardTitle>Bank accounts</CardTitle>
      </CardHeader>
      <CardContent className="space-y-4 text-sm">
        <div className="space-y-2 max-w-xl">
          <div className="text-xs font-semibold text-slate-600">Add bank account</div>
          <div className="grid gap-2 md:grid-cols-3">
            <Input placeholder="Bank name" />
            <Input placeholder="IBAN" />
            <Input placeholder="Currency" defaultValue="CHF" />
          </div>
          <div className="flex justify-end">
            <Button type="button" size="sm">
              Add
            </Button>
          </div>
        </div>

        <div className="mt-4 border border-slate-200 rounded-2xl overflow-hidden">
          {mockAccounts.map((acc) => (
            <div
              key={acc.id}
              className="flex flex-col md:flex-row md:items-center md:justify-between gap-2 px-3 py-2 bg-white border-b last:border-b-0 border-slate-100"
            >
              <div>
                <div className="text-sm font-semibold">{acc.bankName}</div>
                <div className="text-xs text-slate-500">{acc.iban}</div>
              </div>
              <div className="flex items-center gap-2 text-xs">
                <span className="rounded-full bg-slate-50 px-2 py-0.5 border border-slate-200">
                  {acc.currency}
                </span>
                <Button variant="secondary" size="xs" type="button">
                  Edit
                </Button>
              </div>
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  );
}

function CreditCardsSettings() {
  const mockCards = [
    { id: 1, brand: "Visa", last4: "4242", holder: "Brian Hughes", expires: "12/27" },
    { id: 2, brand: "Mastercard", last4: "1881", holder: "XYZ Software", expires: "04/26" }
  ];

  return (
    <Card>
      <CardHeader>
        <CardTitle>Credit cards</CardTitle>
      </CardHeader>
      <CardContent className="space-y-4 text-sm">
        <div className="space-y-2 max-w-xl">
          <div className="text-xs font-semibold text-slate-600">Add credit card</div>
          <div className="grid gap-2 md:grid-cols-3">
            <Input placeholder="Card holder" />
            <Input placeholder="Card number (mock)" />
            <Input placeholder="Expiry (MM/YY)" />
          </div>
          <div className="flex justify-end">
            <Button type="button" size="sm">
              Add
            </Button>
          </div>
        </div>

        <div className="mt-4 border border-slate-200 rounded-2xl overflow-hidden">
          {mockCards.map((card) => (
            <div
              key={card.id}
              className="flex flex-col md:flex-row md:items-center md:justify-between gap-2 px-3 py-2 bg-white border-b last:border-b-0 border-slate-100"
            >
              <div>
                <div className="text-sm font-semibold">
                  {card.brand} •••• {card.last4}
                </div>
                <div className="text-xs text-slate-500">
                  {card.holder} · Expires {card.expires}
                </div>
              </div>
              <div className="flex items-center gap-2 text-xs">
                <Button variant="secondary" size="xs" type="button">
                  Edit
                </Button>
              </div>
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  );
}

function OrganizationSettings() {
  const [isVerified, setIsVerified] = useState(true);

  return (
    <Card>
      <CardHeader>
        <CardTitle>Organization profile</CardTitle>
      </CardHeader>
      <CardContent className="space-y-4 text-sm">
        {isVerified ? (
          <div className="text-xs text-emerald-700 bg-emerald-50 border border-emerald-100 rounded-2xl px-3 py-2">
            All organization details are verified. Editing any field will mark them as unverified.
          </div>
        ) : (
          <div className="text-xs text-amber-700 bg-amber-50 border border-amber-100 rounded-2xl px-3 py-2">
            Changes pending verification by platform admin.
          </div>
        )}

        <div className="grid gap-4 md:grid-cols-2">
          <div className="space-y-1">
            <label className="text-xs font-semibold text-slate-600">
              Organization name {isVerified ? "(verified)" : "(unverified)"}
            </label>
            <Input
              defaultValue="Acme Staffing AG"
              onChange={() => setIsVerified(false)}
            />
          </div>
          <div className="space-y-1">
            <label className="text-xs font-semibold text-slate-600">
              Legal entity type {isVerified ? "(verified)" : "(unverified)"}
            </label>
            <Input
              defaultValue="AG"
              onChange={() => setIsVerified(false)}
            />
          </div>
          <div className="space-y-1">
            <label className="text-xs font-semibold text-slate-600">
              VAT number {isVerified ? "(verified)" : "(unverified)"}
            </label>
            <Input
              defaultValue="CHE-123.456.789"
              onChange={() => setIsVerified(false)}
            />
          </div>
          <div className="space-y-1">
            <label className="text-xs font-semibold text-slate-600">
              Registered address {isVerified ? "(verified)" : "(unverified)"}
            </label>
            <Input
              defaultValue="Main Street 1, 8000 Zurich"
              onChange={() => setIsVerified(false)}
            />
          </div>
        </div>

        <FormFooter />
      </CardContent>
    </Card>
  );
}

function SecurityToggle({ label, description, defaultChecked }: { label: string; description: string; defaultChecked?: boolean }) {
  return (
    <div className="flex items-start justify-between gap-3 py-2">
      <div>
        <div className="text-sm font-medium text-slate-800">{label}</div>
        <div className="text-xs text-slate-500">{description}</div>
      </div>
      <label className="inline-flex items-center cursor-pointer">
        <input type="checkbox" defaultChecked={defaultChecked} className="sr-only" />
        <span className="h-5 w-9 rounded-full bg-slate-200 relative transition-colors peer-checked:bg-blue-500">
          <span className="absolute left-0.5 top-0.5 h-4 w-4 rounded-full bg-white shadow transition-transform" />
        </span>
      </label>
    </div>
  );
}

function SecuritySettings() {
  const copy = useTranslations(SECURITY_TEXTS);
  return (
    <Card>
      <CardHeader>
        <CardTitle>{copy.title}</CardTitle>
      </CardHeader>
      <CardContent className="space-y-6">
        <div>
          <div className="text-sm font-semibold mb-2">{copy.password.title}</div>
          <p className="text-xs text-slate-500 mb-3">{copy.password.hint}</p>
          <div className="space-y-3 max-w-md">
            <div className="space-y-1">
              <label className="text-xs font-semibold text-slate-600">{copy.password.current}</label>
              <Input type="password" placeholder={copy.password.current} />
            </div>
            <div className="space-y-1">
              <label className="text-xs font-semibold text-slate-600">{copy.password.new}</label>
              <Input type="password" placeholder={copy.password.new} />
            </div>
          </div>
        </div>

        <div>
          <div className="text-sm font-semibold mb-2">{copy.preferences.title}</div>
          <div className="space-y-2 max-w-xl">
            <SecurityToggle
              label={copy.preferences.twoStep.label}
              description={copy.preferences.twoStep.description}
              defaultChecked
            />
            <SecurityToggle
              label={copy.preferences.rotate.label}
              description={copy.preferences.rotate.description}
            />
          </div>
        </div>

        <FormFooter />
      </CardContent>
    </Card>
  );
}

function PlanSettings() {
  return (
    <Card>
      <CardHeader>
        <CardTitle>Plan & Billing</CardTitle>
      </CardHeader>
      <CardContent className="space-y-4 text-sm">
        <p className="text-slate-600">Manage your subscription, payment methods, and invoices.</p>
        <div className="rounded-2xl border border-slate-200 p-4 flex items-center justify-between">
          <div>
            <div className="text-sm font-semibold">Pro plan</div>
            <div className="text-xs text-slate-500">Up to 50 active workers and locations.</div>
          </div>
          <div className="text-right">
            <div className="text-base font-semibold">CHF 89 / month</div>
            <Button variant="secondary" className="mt-2 text-xs">Change plan</Button>
          </div>
        </div>
        <FormFooter />
      </CardContent>
    </Card>
  );
}

function NotificationRow({ label, description, defaultChecked }: { label: string; description: string; defaultChecked?: boolean }) {
  return (
    <div className="flex items-center justify-between gap-3 py-1">
      <div>
        <div className="text-sm text-slate-800">{label}</div>
        <div className="text-xs text-slate-500">{description}</div>
      </div>
      <label className="inline-flex items-center cursor-pointer">
        <input type="checkbox" defaultChecked={defaultChecked} className="sr-only" />
        <span className="h-5 w-9 rounded-full bg-slate-200 relative">
          <span className="absolute left-0.5 top-0.5 h-4 w-4 rounded-full bg-white shadow" />
        </span>
      </label>
    </div>
  );
}

function NotificationSettings() {
  return (
    <Card>
      <CardHeader>
        <CardTitle>Notifications</CardTitle>
      </CardHeader>
      <CardContent className="space-y-6 text-sm">
        <div>
          <div className="text-sm font-semibold mb-2">Alerts</div>
          <div className="space-y-2 max-w-xl">
            <NotificationRow label="Communication" description="Get news, announcements, and product updates." defaultChecked />
            <NotificationRow label="Security" description="Get important notifications about your account security." defaultChecked />
            <NotificationRow label="Meetups" description="Get an email when a meetup is posted close to my location." />
          </div>
        </div>

        <div>
          <div className="text-sm font-semibold mb-2">Account activity</div>
          <div className="space-y-2 max-w-xl">
            <NotificationRow label="Someone comments on one of my items" description="Emails about comments" />
            <NotificationRow label="Someone mentions me" description="Mentions in comments or notes" defaultChecked />
            <NotificationRow label="Someone follows me" description="New followers" defaultChecked />
            <NotificationRow label="Someone replies to my job posting" description="Applications and replies" defaultChecked />
          </div>
        </div>

        <FormFooter />
      </CardContent>
    </Card>
  );
}

function TeamSettings() {
  const members = [
    { id: 1, name: "Dejesus Michael", email: "dejesusmichael@mail.org", role: "Admin" },
    { id: 2, name: "Mclaughlin Steele", email: "mclaughlinsteele@mail.me", role: "Admin" },
    { id: 3, name: "Laverne Dodson", email: "laverneododson@mail.ca", role: "Write" },
    { id: 4, name: "Trudy Berg", email: "trudyberg@mail.us", role: "Read" }
  ];

  return (
    <Card>
      <CardHeader>
        <CardTitle>Team</CardTitle>
      </CardHeader>
      <CardContent className="space-y-4 text-sm">
        <div className="space-y-2">
          <label className="text-xs font-semibold text-slate-600">Add team member</label>
          <div className="flex gap-2">
            <Input placeholder="Enter email" className="flex-1" />
            <Button type="button" variant="secondary">Invite</Button>
          </div>
        </div>

        <div className="divide-y divide-slate-100 border border-slate-200 rounded-2xl overflow-hidden mt-4">
          {members.map((m) => (
            <div key={m.id} className="flex items-center justify-between gap-3 px-3 py-2 bg-white">
              <div>
                <div className="text-sm font-semibold">{m.name}</div>
                <div className="text-xs text-slate-500">{m.email}</div>
              </div>
              <div className="flex items-center gap-2">
                <select className="rounded-xl border border-slate-200 px-2 py-1 text-xs bg-white" defaultValue={m.role}>
                  <option>Admin</option>
                  <option>Write</option>
                  <option>Read</option>
                </select>
                <button className="text-xs text-rose-600 hover:text-rose-700">Remove</button>
              </div>
            </div>
          ))}
        </div>

        <FormFooter />
      </CardContent>
    </Card>
  );
}

function AppSettings() {
  const STORAGE_KEY = "app-settings";
  const [language, setLanguage] = useState("en");
  const [timeZone, setTimeZone] = useState("Europe/Zurich");

  React.useEffect(() => {
    try {
      const raw = window.localStorage.getItem(STORAGE_KEY);
      if (raw) {
        const parsed = JSON.parse(raw) as { language?: string; timeZone?: string };
        if (parsed.language) setLanguage(parsed.language);
        if (parsed.timeZone) setTimeZone(parsed.timeZone);
      }
    } catch {
      // ignore
    }
  }, []);

  React.useEffect(() => {
    try {
      window.localStorage.setItem(
        STORAGE_KEY,
        JSON.stringify({ language, timeZone })
      );
    } catch {
      // ignore
    }
  }, [language, timeZone]);

  return (
    <Card>
      <CardHeader>
        <CardTitle>Application Settings</CardTitle>
      </CardHeader>
      <CardContent className="space-y-4 text-sm">
        <p className="text-slate-600">Configure global options for this staffing portal workspace.</p>
        <div className="grid gap-4 md:grid-cols-2 max-w-2xl">
          <div className="space-y-1">
            <label className="text-xs font-semibold text-slate-600">Default language</label>
            <select
              className="rounded-xl border border-slate-200 px-2 py-1 text-sm bg-white"
              value={language}
              onChange={(e) => setLanguage(e.target.value)}
            >
              <option value="en">English</option>
              <option value="de">German</option>
              <option value="tr">Turkish</option>
            </select>
          </div>
          <div className="space-y-1">
            <label className="text-xs font-semibold text-slate-600">Time zone</label>
            <select
              className="rounded-xl border border-slate-200 px-2 py-1 text-sm bg-white"
              value={timeZone}
              onChange={(e) => setTimeZone(e.target.value)}
            >
              <option value="Europe/Zurich">Europe/Zurich</option>
              <option value="Europe/Berlin">Europe/Berlin</option>
              <option value="America/Los_Angeles">America/Los_Angeles</option>
            </select>
          </div>
        </div>
        <FormFooter />
      </CardContent>
    </Card>
  );
}
