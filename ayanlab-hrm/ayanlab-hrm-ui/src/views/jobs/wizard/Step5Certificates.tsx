import React, { useState, useEffect } from 'react';
import { Button } from '../../../ui/components/Button';
import { useCertificatesByRole, useDocumentsByRole } from '../../../data/jobHooks';
import { useLanguage } from '../../../state/i18n';
import type { Language } from '../../../state/i18n';
import { ChevronDown, X, Plus } from 'lucide-react';
import type { JobFormData } from '../types';
import type { StepErrors } from './validation';
import { WIZARD_TITLE_SURFACE, WizardStepHeader, WizardSectionTitle } from './WizardSectionHeading';
import { formatWizard, useJobWizardCopy } from '../i18n/wizardTexts';

const CUSTOM_CERT_ID = '__custom__';
const CUSTOM_DOC_ID = '__custom__';
const CUSTOM_DEPT_ID = '__custom__';

type MultiLang = { en: string; de?: string; tr?: string };

const EDUCATION_LEVELS: Array<{ id: string } & MultiLang> = [
  { id: 'no-education-needed', en: 'No education is needed', de: 'Keine Ausbildung erforderlich', tr: 'Eğitim gerekmez' },
  { id: 'no-education', en: 'No education', de: 'Keine Ausbildung', tr: 'Eğitim yok' },
  { id: 'high-school', en: 'High school', de: 'Realschule', tr: 'Lise' },
  { id: 'diploma', en: 'Diploma', de: 'Diplom', tr: 'Diploma' },
  { id: 'university', en: 'University', de: 'Universität', tr: 'Üniversite' },
  { id: 'bachelor', en: 'Bachelor', de: 'Bachelor', tr: 'Lisans' },
  { id: 'master', en: 'Master', de: 'Master', tr: 'Yüksek lisans' },
  { id: 'phd', en: 'PhD', de: 'Promotion', tr: 'Doktora' },
  { id: 'other', en: 'Other', de: 'Sonstige', tr: 'Diğer' },
];

const DEPARTMENTS: Array<{ id: string } & MultiLang> = [
  { id: 'no-department-needed', en: 'No department is needed', de: 'Keine Abteilung erforderlich', tr: 'Departman gerekmez' },
  { id: 'business', en: 'Business', de: 'Wirtschaft', tr: 'İşletme' },
  { id: 'engineering', en: 'Engineering', de: 'Ingenieurwesen', tr: 'Mühendislik' },
  { id: 'health', en: 'Health', de: 'Gesundheit', tr: 'Sağlık' },
  { id: 'arts', en: 'Arts', de: 'Kunst', tr: 'Sanat' },
  { id: 'science', en: 'Science', de: 'Naturwissenschaft', tr: 'Fen' },
  { id: 'education', en: 'Education', de: 'Bildung', tr: 'Eğitim' },
  { id: 'law', en: 'Law', de: 'Recht', tr: 'Hukuk' },
  { id: 'administration', en: 'Administration', de: 'Verwaltung', tr: 'İdari' },
  { id: 'finance', en: 'Finance', de: 'Finanzen', tr: 'Finans' },
  { id: 'hr', en: 'Human Resources', de: 'Personalwesen', tr: 'İnsan Kaynakları' },
  { id: 'marketing', en: 'Marketing', de: 'Marketing', tr: 'Pazarlama' },
  { id: 'sales', en: 'Sales', de: 'Vertrieb', tr: 'Satış' },
  { id: 'operations', en: 'Operations', de: 'Betrieb', tr: 'Operasyon' },
  { id: 'it', en: 'IT', de: 'IT', tr: 'Bilişim' },
  { id: 'logistics', en: 'Logistics', de: 'Logistik', tr: 'Lojistik' },
  { id: 'customer-service', en: 'Customer Service', de: 'Kundenservice', tr: 'Müşteri Hizmetleri' },
  { id: 'production', en: 'Production', de: 'Produktion', tr: 'Üretim' },
  { id: 'quality', en: 'Quality', de: 'Qualität', tr: 'Kalite' },
  { id: 'research', en: 'Research & Development', de: 'Forschung & Entwicklung', tr: 'Araştırma ve Geliştirme' },
  { id: 'other-dept', en: 'Other', de: 'Sonstige', tr: 'Diğer' },
];

type DrivingLicenceType = {
  id: string;
  en: string;
  de?: string;
  tr?: string;
  descEn: string;
  descDe?: string;
  descTr?: string;
};

const DRIVING_LICENCE_TYPES: DrivingLicenceType[] = [
  { id: 'B', en: 'B', de: 'B', tr: 'B', descEn: 'Cars and small vans (up to 3.5t)', descDe: 'Pkw und kleine Transporter (bis 3,5 t)', descTr: 'Otomobil ve küçük kamyonetler (3,5 t\'a kadar)' },
  { id: 'BE', en: 'BE', de: 'BE', tr: 'BE', descEn: 'B + trailer over 750 kg', descDe: 'B + Anhänger über 750 kg', descTr: 'B + 750 kg üzeri römork' },
  { id: 'C', en: 'C', de: 'C', tr: 'C', descEn: 'Large goods vehicles (over 3.5t)', descDe: 'Schwere Lkw (über 3,5 t)', descTr: 'Ağır yük araçları (3,5 t üzeri)' },
  { id: 'CE', en: 'CE', de: 'CE', tr: 'CE', descEn: 'C + trailer over 750 kg', descDe: 'C + Anhänger über 750 kg', descTr: 'C + 750 kg üzeri römork' },
  { id: 'C1', en: 'C1', de: 'C1', tr: 'C1', descEn: 'Medium-sized vehicles (3.5t–7.5t)', descDe: 'Mittlere Lkw (3,5–7,5 t)', descTr: 'Orta boy araçlar (3,5–7,5 t)' },
  { id: 'C1E', en: 'C1E', de: 'C1E', tr: 'C1E', descEn: 'C1 + trailer over 750 kg', descDe: 'C1 + Anhänger über 750 kg', descTr: 'C1 + 750 kg üzeri römork' },
  { id: 'D', en: 'D', de: 'D', tr: 'D', descEn: 'Buses (8+ passengers)', descDe: 'Busse (8+ Passagiere)', descTr: 'Otobüsler (8+ yolcu)' },
  { id: 'DE', en: 'DE', de: 'DE', tr: 'DE', descEn: 'D + trailer over 750 kg', descDe: 'D + Anhänger über 750 kg', descTr: 'D + 750 kg üzeri römork' },
  { id: 'other-licence', en: 'Other', de: 'Sonstige', tr: 'Diğer', descEn: 'Other licence category', descDe: 'Andere Führerscheinklasse', descTr: 'Diğer ehliyet sınıfı' },
];

const REFERENCE_OPTIONS = [1, 2, 3, 4, 5] as const;

function getLocalizedLabel<T extends MultiLang>(item: T, lang: Language): string {
  if (lang === 'de' && item.de) return item.de;
  if (lang === 'tr' && item.tr) return item.tr;
  return item.en;
}

function getLicenceDescription(licence: DrivingLicenceType, lang: Language): string {
  if (lang === 'de' && licence.descDe) return licence.descDe;
  if (lang === 'tr' && licence.descTr) return licence.descTr;
  return licence.descEn;
}

interface MultiSelectDropdownProps<T extends string> {
  label: string;
  triggerLabel: string;
  options: Array<{ id: T; label: string; description?: string }>;
  selectedIds: string[];
  onToggle: (id: string) => void;
  open: boolean;
  onOpenChange: (open: boolean) => void;
  /** When set, show "Custom" row; not selectable as a chip – when checked, customContent is shown (text box enabled). */
  customOptionId?: string;
  customChecked?: boolean;
  onCustomToggle?: (checked: boolean) => void;
  customContent?: React.ReactNode;
  getSelectedLabel?: (id: string) => string;
  disabled?: boolean;
  loading?: boolean;
  loadingLabel?: string;
  selectedCountLabel?: (count: number) => string;
  customOptionLabel?: string;
}

function MultiSelectDropdown<T extends string>({
  label,
  triggerLabel,
  options,
  selectedIds,
  onToggle,
  open,
  onOpenChange,
  customOptionId,
  customChecked = false,
  onCustomToggle,
  customContent,
  getSelectedLabel,
  disabled,
  loading,
  loadingLabel = 'Loading...',
  selectedCountLabel,
  customOptionLabel = 'Custom',
}: MultiSelectDropdownProps<T>) {
  const total = selectedIds.length;
  return (
    <div>
      <p className={`block text-sm font-semibold text-slate-900 mb-1 ${WIZARD_TITLE_SURFACE}`}>{label}</p>
      <div className="relative">
        <button
          type="button"
          onClick={() => !disabled && onOpenChange(!open)}
          disabled={disabled}
          className="w-full flex items-center justify-between gap-2 rounded-xl border border-slate-200 bg-white px-3 py-2.5 text-left text-sm text-slate-700 hover:border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-60"
        >
          <span className="truncate">
            {loading ? loadingLabel : total === 0 ? triggerLabel : (selectedCountLabel ? selectedCountLabel(total) : `${total} selected`)}
          </span>
          <ChevronDown className={`w-4 h-4 shrink-0 text-slate-400 transition-transform ${open ? 'rotate-180' : ''}`} />
        </button>
        {open && (
          <>
            <div className="fixed inset-0 z-[5]" aria-hidden onClick={() => onOpenChange(false)} />
            <div className="absolute z-10 mt-1 w-full rounded-xl border border-slate-200 bg-white shadow-lg py-2 max-h-64 overflow-y-auto">
              {options.map((opt) => (
                <label
                  key={opt.id}
                  className="flex items-start gap-2 px-3 py-2.5 hover:bg-slate-50 cursor-pointer"
                >
                  <input
                    type="checkbox"
                    checked={selectedIds.includes(opt.id)}
                    onChange={() => onToggle(opt.id)}
                    className="rounded border-slate-300 text-blue-600 mt-0.5 shrink-0"
                  />
                  <div className="min-w-0">
                    <span className="text-sm font-medium text-slate-700">{opt.label}</span>
                    {opt.description && (
                      <p className="text-xs text-slate-500 mt-0.5">{opt.description}</p>
                    )}
                  </div>
                </label>
              ))}
              {customOptionId && (
                <label className="flex items-center gap-2 px-3 py-2 hover:bg-slate-50 cursor-pointer border-t border-slate-100">
                  <input
                    type="checkbox"
                    checked={customChecked}
                    onChange={(e) => onCustomToggle?.(e.target.checked)}
                    className="rounded border-slate-300 text-blue-600"
                  />
                  <span className="text-sm text-slate-700">{customOptionLabel}</span>
                </label>
              )}
              {customChecked && customContent}
            </div>
          </>
        )}
      </div>
      {total > 0 && getSelectedLabel && (
        <div className="flex flex-wrap gap-1.5 mt-2">
          {selectedIds.map((id) => (
            <span
              key={id}
              className="inline-flex items-center gap-1 px-2 py-1 rounded-lg bg-blue-50 text-blue-800 text-xs font-medium"
            >
              {getSelectedLabel(id)}
              <button
                type="button"
                onClick={() => onToggle(id)}
                className="hover:bg-blue-100 rounded p-0.5"
                aria-label={`Remove ${getSelectedLabel(id)}`}
              >
                <X className="w-3.5 h-3.5" />
              </button>
            </span>
          ))}
        </div>
      )}
    </div>
  );
}

interface Step5CertificatesProps {
  formData: JobFormData;
  updateFormData: (updates: Partial<JobFormData>) => void;
  onNext?: () => void;
  onBack?: () => void;
  canGoBack?: boolean;
  errors?: StepErrors;
}

export function Step5Certificates({ formData, updateFormData, onNext, onBack, canGoBack = false }: Step5CertificatesProps) {
  const w = useJobWizardCopy();
  const c = w.common;
  const s = w.step5;
  const { language } = useLanguage();
  const { data: certificates = [], isLoading: certsLoading, isError: certsError } = useCertificatesByRole(formData.selectedRoleId || null, language);
  const { data: documents = [], isLoading: docsLoading } = useDocumentsByRole(formData.selectedRoleId || null);

  const [certsOpen, setCertsOpen] = useState(false);
  const [docsOpen, setDocsOpen] = useState(false);
  const [licenceOpen, setLicenceOpen] = useState(false);
  const [eduOpen, setEduOpen] = useState(false);
  const [deptOpen, setDeptOpen] = useState(false);

  const [customCertName, setCustomCertName] = useState('');
  const [customCertRequired, setCustomCertRequired] = useState(true);
  const [customDocName, setCustomDocName] = useState('');
  const [customDeptName, setCustomDeptName] = useState('');
  const [customCertOpen, setCustomCertOpen] = useState(false);
  const [customDocOpen, setCustomDocOpen] = useState(false);
  const [customDeptOpen, setCustomDeptOpen] = useState(false);

  useEffect(() => {
    if ((formData.customCertificates?.length ?? 0) > 0) setCustomCertOpen(true);
  }, []);
  useEffect(() => {
    if ((formData.customDocuments?.length ?? 0) > 0) setCustomDocOpen(true);
  }, []);
  useEffect(() => {
    if ((formData.customDepartments?.length ?? 0) > 0) setCustomDeptOpen(true);
  }, []);

  const selectedCerts = formData.selectedCertificates ?? [];
  const customCerts = formData.customCertificates ?? [];
  const noCertificates = formData.noCertificates ?? false;
  const selectedDocs = formData.selectedDocuments ?? [];
  const customDocs = formData.customDocuments ?? [];
  const noDocuments = formData.noDocuments ?? false;
  const educationLevels = formData.selectedEducationLevels ?? [];
  const departments = formData.selectedDepartments ?? [];
  const customDepts = formData.customDepartments ?? [];
  const selectedLicences = formData.selectedDrivingLicences ?? [];
  const noDrivingLicence = formData.noDrivingLicence ?? false;
  const referenceCount = formData.referenceCount ?? 0;

  const certOptions = certificates.map((c: { id: string; name: string }) => ({ id: c.id, label: c.name }));
  const docOptions = documents.map((d: { id: string; name: string }) => ({ id: d.id, label: d.name }));
  const eduOptions = EDUCATION_LEVELS.map((e) => ({ id: e.id, label: getLocalizedLabel(e, language) }));
  const deptOptions = DEPARTMENTS.map((d) => ({ id: d.id, label: getLocalizedLabel(d, language) }));
  const licenceOptions = DRIVING_LICENCE_TYPES.map((l) => ({
    id: l.id,
    label: getLocalizedLabel(l, language),
    description: getLicenceDescription(l, language),
  }));

  const certSelectedIds = [...selectedCerts, ...customCerts.map((_, i) => `custom-cert-${i}`)];
  const docSelectedIds = [...selectedDocs, ...customDocs.map((_, i) => `custom-doc-${i}`)];
  const deptSelectedIds = [...departments, ...customDepts.map((_, i) => `custom-dept-${i}`)];

  const toggleCertificate = (id: string) => {
    if (noCertificates) return;
    if (id.startsWith('custom-cert-')) {
      const i = parseInt(id.replace('custom-cert-', ''), 10);
      if (!Number.isNaN(i)) removeCustomCertificate(i);
      return;
    }
    const next = selectedCerts.includes(id)
      ? selectedCerts.filter((c) => c !== id)
      : [...selectedCerts, id];
    updateFormData({ selectedCertificates: next });
  };

  const setNoCertificates = (value: boolean) => {
    updateFormData({
      noCertificates: value,
      ...(value ? { selectedCertificates: [], customCertificates: [] } : {}),
    });
  };

  const addCustomCertificate = () => {
    const name = customCertName.trim();
    if (!name) return;
    updateFormData({ customCertificates: [...customCerts, { name, required: customCertRequired }] });
    setCustomCertName('');
    setCustomCertRequired(true);
  };

  const removeCustomCertificate = (index: number) => {
    updateFormData({ customCertificates: customCerts.filter((_, i) => i !== index) });
  };

  const toggleDocument = (id: string) => {
    if (noDocuments) return;
    if (id.startsWith('custom-doc-')) {
      const i = parseInt(id.replace('custom-doc-', ''), 10);
      if (!Number.isNaN(i)) removeCustomDocument(i);
      return;
    }
    const next = selectedDocs.includes(id)
      ? selectedDocs.filter((d) => d !== id)
      : [...selectedDocs, id];
    updateFormData({ selectedDocuments: next });
  };

  const setNoDocuments = (value: boolean) => {
    updateFormData({
      noDocuments: value,
      ...(value ? { selectedDocuments: [], customDocuments: [] } : {}),
    });
  };

  const addCustomDocument = () => {
    const name = customDocName.trim();
    if (!name) return;
    updateFormData({ customDocuments: [...customDocs, { name }] });
    setCustomDocName('');
  };

  const removeCustomDocument = (index: number) => {
    updateFormData({ customDocuments: customDocs.filter((_, i) => i !== index) });
  };

  const toggleEducation = (id: string) => {
    const next = educationLevels.includes(id)
      ? educationLevels.filter((e) => e !== id)
      : [...educationLevels, id];
    updateFormData({ selectedEducationLevels: next });
  };

  const toggleDepartment = (id: string) => {
    if (id.startsWith('custom-dept-')) {
      const i = parseInt(id.replace('custom-dept-', ''), 10);
      if (!Number.isNaN(i)) removeCustomDepartment(i);
      return;
    }
    const next = departments.includes(id)
      ? departments.filter((d) => d !== id)
      : [...departments, id];
    updateFormData({ selectedDepartments: next });
  };

  const setNoDrivingLicence = (value: boolean) => {
    updateFormData({
      noDrivingLicence: value,
      ...(value ? { selectedDrivingLicences: [] } : {}),
    });
  };

  const toggleDrivingLicence = (id: string) => {
    if (noDrivingLicence) return;
    const next = selectedLicences.includes(id)
      ? selectedLicences.filter((l) => l !== id)
      : [...selectedLicences, id];
    updateFormData({ selectedDrivingLicences: next });
  };

  const addCustomDepartment = () => {
    const name = customDeptName.trim();
    if (!name) return;
    updateFormData({ customDepartments: [...customDepts, name] });
    setCustomDeptName('');
  };

  const removeCustomDepartment = (index: number) => {
    updateFormData({ customDepartments: customDepts.filter((_, i) => i !== index) });
  };

  const getCertLabel = (id: string) => {
    const m = id.match(/^custom-cert-(\d+)$/);
    if (m) return customCerts[parseInt(m[1], 10)]?.name ?? id;
    return certificates.find((c: { id: string }) => c.id === id)?.name ?? id;
  };
  const getDocLabel = (id: string) => {
    const m = id.match(/^custom-doc-(\d+)$/);
    if (m) return customDocs[parseInt(m[1], 10)]?.name ?? id;
    return documents.find((d: { id: string }) => d.id === id)?.name ?? id;
  };
  const getEduLabel = (id: string) => {
    const e = EDUCATION_LEVELS.find((x) => x.id === id);
    return e ? getLocalizedLabel(e, language) : id;
  };
  const getDeptLabel = (id: string) => {
    const m = id.match(/^custom-dept-(\d+)$/);
    if (m) return customDepts[parseInt(m[1], 10)] ?? id;
    const d = DEPARTMENTS.find((x) => x.id === id);
    return d ? getLocalizedLabel(d, language) : id;
  };
  const getLicenceLabel = (id: string) => {
    const l = DRIVING_LICENCE_TYPES.find((x) => x.id === id);
    return l ? getLocalizedLabel(l, language) : id;
  };

  const dropdownI18n = {
    loadingLabel: c.loading,
    selectedCountLabel: (count: number) => formatWizard(s.selectedCount, { count }),
    customOptionLabel: c.custom,
  };

  return (
    <div className="space-y-6">
      <WizardStepHeader title={s.title} subtitle={s.subtitle} />

      <div className="bg-white rounded-2xl border border-slate-200 p-6 space-y-6">
        {/* Certificates */}
        <div>
          <WizardSectionTitle title={s.certificates} description={s.certificatesHint} />
          <label className="flex items-center gap-2 p-2 mb-2">
            <input
              type="checkbox"
              checked={noCertificates}
              onChange={(e) => setNoCertificates(e.target.checked)}
              className="rounded border-slate-300"
            />
            <span className="text-sm font-medium text-slate-700">{s.noCertificates}</span>
          </label>
          {!noCertificates && (
            <MultiSelectDropdown
              {...dropdownI18n}
              label={s.chooseCertificates}
              triggerLabel={s.chooseCertificates}
              options={certOptions}
              selectedIds={certSelectedIds}
              onToggle={toggleCertificate}
              open={certsOpen}
              onOpenChange={setCertsOpen}
              customOptionId={CUSTOM_CERT_ID}
              customChecked={customCertOpen}
              onCustomToggle={(checked) => {
                setCustomCertOpen(checked);
                if (!checked) updateFormData({ customCertificates: [] });
              }}
              getSelectedLabel={getCertLabel}
              disabled={!formData.selectedRoleId}
              loading={certsLoading}
              customContent={
                (
                  <div className="border-t border-slate-100 px-3 py-2 bg-slate-50/50 space-y-2">
                    <div className="flex gap-2 items-center">
                      <input
                        type="text"
                        value={customCertName}
                        onChange={(e) => setCustomCertName(e.target.value)}
                        placeholder={s.certificateNamePlaceholder}
                        className="flex-1 rounded-lg border border-slate-200 px-2 py-1.5 text-sm"
                      />
                      <label className="flex items-center gap-1 text-xs text-slate-600 shrink-0">
                        <input
                          type="checkbox"
                          checked={customCertRequired}
                          onChange={(e) => setCustomCertRequired(e.target.checked)}
                          className="rounded border-slate-300"
                        />
                        {s.customRequired}
                      </label>
                      <button
                        type="button"
                        onClick={addCustomCertificate}
                        className="p-2 rounded-lg border border-slate-200 bg-white hover:bg-slate-50 text-slate-600"
                        aria-label="Add custom certificate"
                      >
                        <Plus className="w-4 h-4" />
                      </button>
                    </div>
                    {customCerts.length > 0 && (
                      <ul className="text-xs space-y-1">
                        {customCerts.map((c, i) => (
                          <li key={i} className="flex items-center justify-between">
                            <span>{c.name} {c.required !== false ? '(required)' : ''}</span>
                            <button type="button" onClick={() => removeCustomCertificate(i)} className="text-slate-400 hover:text-red-600">
                              <X className="w-3.5 h-3.5" />
                            </button>
                          </li>
                        ))}
                      </ul>
                    )}
                  </div>
                )
              }
            />
          )}
          {!formData.selectedRoleId && !noCertificates && (
            <p className="text-xs text-slate-500 mt-1">{s.selectRoleHint}</p>
          )}
          {formData.selectedRoleId && !noCertificates && !certsLoading && certsError && (
            <p className="text-xs text-red-600 mt-1">{s.certificatesLoadFail}</p>
          )}
          {formData.selectedRoleId && !noCertificates && !certsLoading && !certsError && certificates.length === 0 && (
            <p className="text-xs text-slate-500 mt-1">{s.noCertificatesForRole}</p>
          )}
        </div>

        <hr className="border-t border-slate-200" />

        {/* Required Documents */}
        <div>
          <WizardSectionTitle title={s.documents} description={s.documentsHint} />
          <label className="flex items-center gap-2 p-2 mb-2">
            <input
              type="checkbox"
              checked={noDocuments}
              onChange={(e) => setNoDocuments(e.target.checked)}
              className="rounded border-slate-300"
            />
            <span className="text-sm font-medium text-slate-700">{s.noDocuments}</span>
          </label>
          {!noDocuments && (
            <MultiSelectDropdown
              {...dropdownI18n}
              label={s.chooseDocuments}
              triggerLabel={s.chooseDocuments}
              options={docOptions}
              selectedIds={docSelectedIds}
              onToggle={toggleDocument}
              open={docsOpen}
              onOpenChange={setDocsOpen}
              customOptionId={CUSTOM_DOC_ID}
              customChecked={customDocOpen}
              onCustomToggle={(checked) => {
                setCustomDocOpen(checked);
                if (!checked) updateFormData({ customDocuments: [] });
              }}
              getSelectedLabel={getDocLabel}
              disabled={!formData.selectedRoleId}
              loading={docsLoading}
              customContent={
                <div className="border-t border-slate-100 px-3 py-2 bg-slate-50/50 flex gap-2 items-center">
                  <input
                    type="text"
                    value={customDocName}
                    onChange={(e) => setCustomDocName(e.target.value)}
                    placeholder={s.documentNamePlaceholder}
                    className="flex-1 rounded-lg border border-slate-200 px-2 py-1.5 text-sm"
                  />
                  <button
                    type="button"
                    onClick={addCustomDocument}
                    className="p-2 rounded-lg border border-slate-200 bg-white hover:bg-slate-50 text-slate-600"
                    aria-label="Add custom document"
                  >
                    <Plus className="w-4 h-4" />
                  </button>
                </div>
              }
            />
          )}
          {!formData.selectedRoleId && !noDocuments && (
            <p className="text-xs text-slate-500 mt-1">{s.selectRoleDocsHint}</p>
          )}
        </div>

        <hr className="border-t border-slate-200" />

        {/* Driving Licence */}
        <div>
          <WizardSectionTitle title={s.drivingLicence} description={s.drivingLicenceHint} />
          <label className="flex items-center gap-2 p-2 mb-2">
            <input
              type="checkbox"
              checked={noDrivingLicence}
              onChange={(e) => setNoDrivingLicence(e.target.checked)}
              className="rounded border-slate-300"
            />
            <span className="text-sm font-medium text-slate-700">{s.noDrivingLicence}</span>
          </label>
          {!noDrivingLicence && (
            <MultiSelectDropdown
              {...dropdownI18n}
              label={s.chooseLicence}
              triggerLabel={s.chooseLicence}
              options={licenceOptions}
              selectedIds={selectedLicences}
              onToggle={toggleDrivingLicence}
              open={licenceOpen}
              onOpenChange={setLicenceOpen}
              getSelectedLabel={getLicenceLabel}
            />
          )}
        </div>

        <hr className="border-t border-slate-200" />

        {/* Education */}
        <div>
          <WizardSectionTitle title={s.education} />
          <MultiSelectDropdown
            {...dropdownI18n}
            label={s.chooseEducation}
            triggerLabel={s.chooseEducation}
            options={eduOptions}
            selectedIds={educationLevels}
            onToggle={toggleEducation}
            open={eduOpen}
            onOpenChange={setEduOpen}
            getSelectedLabel={getEduLabel}
          />
        </div>

        <hr className="border-t border-slate-200" />

        {/* Departments */}
        <div>
          <WizardSectionTitle title={s.departments} />
          <MultiSelectDropdown
            {...dropdownI18n}
            label={s.chooseDepartments}
            triggerLabel={s.chooseDepartments}
            options={deptOptions}
            selectedIds={deptSelectedIds}
            onToggle={toggleDepartment}
            open={deptOpen}
            onOpenChange={setDeptOpen}
            customOptionId={CUSTOM_DEPT_ID}
            customChecked={customDeptOpen}
            onCustomToggle={(checked) => {
              setCustomDeptOpen(checked);
              if (!checked) updateFormData({ customDepartments: [] });
            }}
            getSelectedLabel={getDeptLabel}
            customContent={
              <div className="border-t border-slate-100 px-3 py-2 bg-slate-50/50 flex gap-2 items-center">
                <input
                  type="text"
                  value={customDeptName}
                  onChange={(e) => setCustomDeptName(e.target.value)}
                  placeholder={s.departmentNamePlaceholder}
                  className="flex-1 rounded-lg border border-slate-200 px-2 py-1.5 text-sm"
                />
                <button
                  type="button"
                  onClick={addCustomDepartment}
                  className="p-2 rounded-lg border border-slate-200 bg-white hover:bg-slate-50 text-slate-600"
                  aria-label="Add custom department"
                >
                  <Plus className="w-4 h-4" />
                </button>
              </div>
            }
          />
        </div>

        <hr className="border-t border-slate-200" />

        {/* References needed */}
        <div>
          <WizardSectionTitle title={s.references} />
          <label className="block text-sm font-medium text-slate-700 mb-1">{s.howManyReferences}</label>
          <select
            value={referenceCount}
            onChange={(e) => updateFormData({ referenceCount: Number(e.target.value) })}
            className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 min-w-[120px]"
          >
            <option value={0}>{c.none}</option>
            {REFERENCE_OPTIONS.map((n) => (
              <option key={n} value={n}>
                {n}
              </option>
            ))}
          </select>
        </div>

        <hr className="border-t border-slate-200" />

        {/* Navigation Buttons */}
        {(onNext || onBack) && (
          <div className="flex justify-between items-center gap-4 pt-6 pb-2">
            <Button variant="secondary" onClick={onBack} disabled={!canGoBack}>
              {c.back}
            </Button>
            <Button onClick={onNext}>{c.next}</Button>
          </div>
        )}
      </div>
    </div>
  );
}
