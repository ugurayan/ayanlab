import React, { useState, useMemo } from 'react';
import { Button } from '../../../ui/components/Button';
import { useSkillsByJobRole } from '../../../data/jobHooks';
import { useLanguage } from '../../../state/i18n';
import { ChevronDown, X, Search, Loader2, Plus } from 'lucide-react';
import type { JobFormData } from '../types';
import type { StepErrors } from './validation';
import { WizardStepHeader, WizardSectionTitle, WizardFieldLabel } from './WizardSectionHeading';
import { formatWizard, useJobWizardCopy } from '../i18n/wizardTexts';

import { SPOKEN_LANGUAGE_OPTIONS } from "../../../i18n/localeCatalog";

const LANGUAGE_OPTIONS = SPOKEN_LANGUAGE_OPTIONS;
const PROFICIENCY_LEVELS = ['A1', 'A2', 'B1', 'B2', 'C1', 'C2', 'Native'] as const;

interface Step4RequirementsProps {
  formData: JobFormData;
  updateFormData: (updates: Partial<JobFormData>) => void;
  onNext?: () => void;
  onBack?: () => void;
  canGoBack?: boolean;
  errors?: StepErrors;
}

function FieldError({ message }: { message?: string }) {
  if (!message) return null;
  return <p className="text-xs text-red-600 mt-1">{message}</p>;
}

export function Step4Requirements({ formData, updateFormData, onNext, onBack, canGoBack = false, errors = {} }: Step4RequirementsProps) {
  const w = useJobWizardCopy();
  const c = w.common;
  const s = w.step4;
  const { language } = useLanguage();
  const { data: skills = [], isLoading: skillsLoading, isError: skillsError } = useSkillsByJobRole(formData.selectedRoleId || null, language);
  const [skillsOpen, setSkillsOpen] = useState(false);
  const [skillsSearch, setSkillsSearch] = useState('');
  const [customSkillInput, setCustomSkillInput] = useState('');
  const [langToAdd, setLangToAdd] = useState('');
  const [levelToAdd, setLevelToAdd] = useState<string>(PROFICIENCY_LEVELS[0]);
  const [certificationToAdd, setCertificationToAdd] = useState(false);

  const skillRequirements = formData.skillRequirements ?? [];
  const languageRequirements = formData.languageRequirements ?? [];

  const filteredSkills = useMemo(() => {
    if (!skillsSearch.trim()) return skills;
    const q = skillsSearch.trim().toLowerCase();
    return skills.filter((s: { id: string; name: string }) => s.name.toLowerCase().includes(q));
  }, [skills, skillsSearch]);

  const selectedSkillIds = new Set(skillRequirements.map((s) => s.skillId));

  const toggleSkill = (id: string) => {
    const exists = skillRequirements.some((s) => s.skillId === id);
    if (exists) {
      const next = skillRequirements.filter((s) => s.skillId !== id);
      updateFormData({
        skillRequirements: next,
        selectedSkills: next.map((s) => s.skillId),
      });
    } else {
      const next = [
        ...skillRequirements,
        { skillId: id, expertiseLevel: 'same-job-title', experienceDuration: 'none' },
      ];
      updateFormData({
        skillRequirements: next,
        selectedSkills: next.map((s) => s.skillId),
      });
    }
  };

  const updateSkillRequirement = (
    skillId: string,
    field: 'expertiseLevel' | 'experienceDuration' | 'customValue' | 'customUnit',
    value: string | number | 'months' | 'years'
  ) => {
    const next = skillRequirements.map((s) => {
      if (s.skillId !== skillId) return s;
      const updated = { ...s, [field]: value };
      if (field === 'experienceDuration' && value === 'custom') {
        updated.customValue = updated.customValue ?? 1;
        updated.customUnit = updated.customUnit ?? 'months';
      }
      return updated;
    });
    updateFormData({ skillRequirements: next, selectedSkills: next.map((s) => s.skillId) });
  };

  const removeSkillRequirement = (skillId: string) => {
    const next = skillRequirements.filter((s) => s.skillId !== skillId);
    updateFormData({ skillRequirements: next, selectedSkills: next.map((s) => s.skillId) });
  };

  const addLanguage = () => {
    const lang = (langToAdd || LANGUAGE_OPTIONS[0]).trim();
    if (!lang) return;
    if (languageRequirements.some((l) => l.language === lang)) return; // already added
    const next = [...languageRequirements, { language: lang, level: levelToAdd, certificationNeeded: certificationToAdd }];
    updateFormData({
      languageRequirements: next,
      selectedLanguages: next.map((l) => l.language),
    });
    setLangToAdd('');
    setCertificationToAdd(false);
  };

  const removeLanguage = (index: number) => {
    const next = languageRequirements.filter((_, i) => i !== index);
    updateFormData({
      languageRequirements: next,
      selectedLanguages: next.map((l) => l.language),
    });
  };

  const getSkillName = (req: { skillId: string; skillName?: string }) =>
    req.skillName ?? skills.find((s: { id: string }) => s.id === req.skillId)?.name ?? req.skillId;

  const addCustomSkill = () => {
    const name = customSkillInput.trim();
    if (!name) return;
    const customId = `custom-${Date.now()}-${Math.random().toString(36).slice(2)}`;
    const next = [
      ...skillRequirements,
      { skillId: customId, skillName: name, expertiseLevel: 'same-job-title', experienceDuration: 'none' },
    ];
    updateFormData({
      skillRequirements: next,
      selectedSkills: next.map((s) => s.skillId),
    });
    setCustomSkillInput('');
  };

  return (
    <div className="space-y-6">
      <WizardStepHeader title={s.title} subtitle={s.subtitle} />

      <div className="bg-white rounded-2xl border border-slate-200 p-6 space-y-6">
        {/* Required Skills: multi-select + list with expertise & experience */}
        <div>
          <WizardSectionTitle title={s.requiredSkills} description={s.requiredSkillsHint} required />
          {!formData.selectedRoleId ? (
            <p className="text-sm text-slate-500 py-3 rounded-xl bg-slate-50 border border-slate-200 px-3">{s.selectRoleFirst}</p>
          ) : skillsError && skills.length === 0 ? (
            <p className="text-sm text-amber-800 py-3 rounded-xl bg-amber-50 border border-amber-200 px-3">{s.skillsLoadWarning}</p>
          ) : (
            <>
              <div className="relative">
                <button
                  type="button"
                  onClick={() => setSkillsOpen((o) => !o)}
                  className="w-full flex items-center justify-between gap-2 rounded-xl border border-slate-200 bg-white px-3 py-2.5 text-left text-sm text-slate-700 hover:border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                >
                  <span className="truncate">
                    {skillsLoading
                      ? s.loadingSkills
                      : selectedSkillIds.size === 0
                        ? s.chooseSkills
                        : formatWizard(s.skillsSelected, { count: selectedSkillIds.size })}
                  </span>
                  {skillsLoading ? (
                    <Loader2 className="w-4 h-4 shrink-0 animate-spin text-slate-400" />
                  ) : (
                    <ChevronDown className={`w-4 h-4 shrink-0 text-slate-400 transition-transform ${skillsOpen ? 'rotate-180' : ''}`} />
                  )}
                </button>
                {skillsOpen && (
                  <>
                    <div className="fixed inset-0 z-[5]" aria-hidden onClick={() => setSkillsOpen(false)} />
                    <div className="absolute z-10 mt-1 w-full rounded-xl border border-slate-200 bg-white shadow-lg" style={{ minHeight: '200px' }}>
                      <div className="p-2 border-b border-slate-100">
                        <div className="relative">
                          <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
                          <input
                            type="text"
                            value={skillsSearch}
                            onChange={(e) => setSkillsSearch(e.target.value)}
                            placeholder={s.searchSkills}
                            className="w-full pl-8 pr-3 py-2 rounded-lg border border-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                          />
                        </div>
                      </div>
                      <div className="max-h-56 overflow-y-auto p-2">
                        {filteredSkills.length > 0 ? (
                          <ul className="space-y-0.5">
                            {filteredSkills.map((skill: { id: string; name: string }) => (
                              <li key={skill.id}>
                                <label className="flex items-center gap-2 p-2 rounded-lg hover:bg-slate-50 cursor-pointer">
                                  <input
                                    type="checkbox"
                                    checked={selectedSkillIds.has(skill.id)}
                                    onChange={() => toggleSkill(skill.id)}
                                    className="rounded border-slate-300 text-blue-600 focus:ring-blue-500"
                                  />
                                  <span className="text-sm text-slate-700">{skill.name}</span>
                                </label>
                              </li>
                            ))}
                          </ul>
                        ) : formData.selectedRoleId && !skillsLoading ? (
                          <p className="text-sm text-slate-500 py-2 text-center">{s.noSkillsMatch}</p>
                        ) : null}
                      </div>
                      {/* Add your own skill */}
                      <div className="p-2 border-t border-slate-200 bg-slate-50/50">
                        <p className="text-xs font-medium text-slate-600 mb-1.5">{s.addOwnSkill}</p>
                        <div className="flex gap-2">
                          <input
                            type="text"
                            value={customSkillInput}
                            onChange={(e) => setCustomSkillInput(e.target.value)}
                            onKeyDown={(e) => e.key === 'Enter' && (e.preventDefault(), addCustomSkill())}
                            placeholder={s.skillNamePlaceholder}
                            className="flex-1 min-w-0 rounded-lg border border-slate-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                          />
                          <Button type="button" size="sm" onClick={addCustomSkill} disabled={!customSkillInput.trim()}>
                            <Plus className="w-4 h-4 mr-1 inline" />
                            {c.add}
                          </Button>
                        </div>
                      </div>
                    </div>
                  </>
                )}
              </div>

              {/* List of selected skills with expertise + experience */}
              {skillRequirements.length > 0 && (
                <div className="mt-4 rounded-xl border border-slate-200 overflow-hidden">
                  <div className="bg-slate-50 px-3 py-2 grid grid-cols-12 gap-2 text-xs font-medium text-slate-600 border-b border-slate-200">
                    <div className="col-span-4">{s.skillColumn}</div>
                    <div className="col-span-4">{s.expertiseColumn}</div>
                    <div className="col-span-4">{s.experienceColumn}</div>
                  </div>
                  <ul className="divide-y divide-slate-100">
                    {skillRequirements.map((req) => (
                      <li key={req.skillId} className="grid grid-cols-12 gap-2 items-center px-3 py-2 bg-white hover:bg-slate-50/50">
                        <div className="col-span-4 text-sm font-medium text-slate-800">{getSkillName(req)}</div>
                        <div className="col-span-4">
                          <select
                            value={req.expertiseLevel || ''}
                            onChange={(e) => updateSkillRequirement(req.skillId, 'expertiseLevel', e.target.value)}
                            className="w-full rounded-lg border border-slate-200 px-2 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                          >
                            {s.expertiseOptions.map((opt) => (
                              <option key={opt.value} value={opt.value}>
                                {opt.label}
                              </option>
                            ))}
                          </select>
                        </div>
                        <div className="col-span-3 flex flex-wrap items-center gap-1">
                          <select
                            value={req.experienceDuration || 'none'}
                            onChange={(e) => updateSkillRequirement(req.skillId, 'experienceDuration', e.target.value)}
                            className="flex-1 min-w-0 rounded-lg border border-slate-200 px-2 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                          >
                            {s.experienceOptions.map((opt) => (
                              <option key={opt.value} value={opt.value}>
                                {opt.label}
                              </option>
                            ))}
                          </select>
                          {req.experienceDuration === 'custom' && (
                            <span className="flex items-center gap-1">
                              <input
                                type="number"
                                min={1}
                                value={req.customValue ?? ''}
                                onChange={(e) => updateSkillRequirement(req.skillId, 'customValue', e.target.value === '' ? 0 : Number(e.target.value))}
                                className="w-14 rounded-lg border border-slate-200 px-2 py-1.5 text-sm"
                                placeholder="0"
                              />
                              <select
                                value={req.customUnit ?? 'months'}
                                onChange={(e) => updateSkillRequirement(req.skillId, 'customUnit', e.target.value as 'months' | 'years')}
                                className="rounded-lg border border-slate-200 px-2 py-1.5 text-sm"
                              >
                                <option value="months">{s.months}</option>
                                <option value="years">{s.years}</option>
                              </select>
                            </span>
                          )}
                        </div>
                        <div className="col-span-1 flex justify-end">
                          <button
                            type="button"
                            onClick={() => removeSkillRequirement(req.skillId)}
                            className="p-1 rounded hover:bg-red-50 text-slate-400 hover:text-red-600"
                            aria-label={`Remove ${getSkillName(req)}`}
                          >
                            <X className="w-4 h-4" />
                          </button>
                        </div>
                      </li>
                    ))}
                  </ul>
                </div>
              )}
            </>
          )}
        </div>

        {/* Languages: dropdown + level A1–C2 / Native + certification needed + Add, then list */}
        <div>
          <WizardSectionTitle title={s.languages} description={s.languagesHint} />
          <div className="flex flex-wrap items-end gap-2">
            <select
              value={langToAdd}
              onChange={(e) => setLangToAdd(e.target.value)}
              className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 min-w-[140px]"
            >
              <option value="">{s.selectLanguage}</option>
              {LANGUAGE_OPTIONS.map((lang) => (
                <option key={lang} value={lang}>
                  {lang}
                </option>
              ))}
            </select>
            <select
              value={levelToAdd}
              onChange={(e) => setLevelToAdd(e.target.value)}
              className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 min-w-[100px]"
            >
              {PROFICIENCY_LEVELS.map((l) => (
                <option key={l} value={l}>
                  {l}
                </option>
              ))}
            </select>
            <label className="flex items-center gap-2 px-2 py-2 text-sm text-slate-700">
              <input
                type="checkbox"
                checked={certificationToAdd}
                onChange={(e) => setCertificationToAdd(e.target.checked)}
                className="rounded border-slate-300 text-blue-600 focus:ring-blue-500"
              />
              {c.certificationNeeded}
            </label>
            <Button type="button" variant="secondary" size="sm" onClick={addLanguage}>
              <Plus className="w-4 h-4 mr-1" />
              {c.add}
            </Button>
          </div>
          {languageRequirements.length > 0 && (
            <ul className="mt-3 space-y-2">
              {languageRequirements.map((entry, index) => (
                <li
                  key={`${entry.language}-${entry.level}-${index}`}
                  className="flex items-center justify-between rounded-lg border border-slate-200 bg-slate-50/50 px-3 py-2 text-sm"
                >
                  <span className="font-medium text-slate-800">
                    {entry.language} – {entry.level}
                    {entry.certificationNeeded && (
                      <span className="ml-2 text-xs font-normal text-slate-600">{s.certificationRequired}</span>
                    )}
                  </span>
                  <button
                    type="button"
                    onClick={() => removeLanguage(index)}
                    className="p-1 rounded hover:bg-red-50 text-slate-400 hover:text-red-600"
                    aria-label={`Remove ${entry.language}`}
                  >
                    <X className="w-4 h-4" />
                  </button>
                </li>
              ))}
            </ul>
          )}
          <FieldError message={errors.selectedLanguages} />
        </div>

        {/* Job Duties – separated by horizontal line */}
        <div className="pt-6 mt-6 border-t border-slate-200">
          <WizardFieldLabel>{s.jobDuties}</WizardFieldLabel>
          <textarea
            className="w-full rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 min-h-[120px]"
            value={formData.jobDuties}
            onChange={(e) => updateFormData({ jobDuties: e.target.value })}
            placeholder={s.jobDutiesPlaceholder}
          />
        </div>

        {/* Additional Instructions – separated by horizontal line */}
        <div className="pt-6 mt-6 border-t border-slate-200">
          <WizardFieldLabel>{s.additionalInstructions}</WizardFieldLabel>
          <textarea
            className="w-full rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 min-h-[100px]"
            value={formData.otherInstructions}
            onChange={(e) => updateFormData({ otherInstructions: e.target.value })}
            placeholder={s.additionalInstructionsPlaceholder}
          />
        </div>

        {/* Navigation Buttons */}
        {(onNext || onBack) && (
          <div className="flex justify-between items-center gap-4 pt-6 mt-6 pb-2 border-t border-slate-200">
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
