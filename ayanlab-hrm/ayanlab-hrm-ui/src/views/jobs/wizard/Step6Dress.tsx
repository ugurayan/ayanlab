import React, { useState, useRef } from 'react';
import { Button } from '../../../ui/components/Button';
import { Modal } from '../../../ui/components/Modal';
import { SimpleRichEditor, sanitizeHtml } from '../../../ui/components/SimpleRichEditor';
import { Minus, X } from 'lucide-react';
import { useTranslations } from '../../../state/i18n';
import { TEXTS } from '../i18n';
import type { JobFormData } from '../types';
import type { StepErrors } from './validation';

const BASE_IMG = 'https://storage.googleapis.com/public-buckets/staff-iq/';
const DRESS_CODE_OPTIONS: Array<{ id: JobFormData['selectedDressType']; image: string | null }> = [
  { id: 'casual', image: `${BASE_IMG}casual_10.jpeg` },
  { id: 'smart-casual', image: `${BASE_IMG}smart-casual_10.jpeg` },
  { id: 'business', image: `${BASE_IMG}office_10.jpeg` },
  { id: 'dress-provided', image: `${BASE_IMG}dress-provided_10.jpeg` },
  { id: 'custom-uniform', image: null },
  { id: 'no-preference', image: null },
];

const DRESS_ITEMS = [
  'T-shirt', 'Polo Shirt', 'Button-up Shirt', 'Blouse', 'Dress Shirt',
  'Pants', 'Jeans', 'Khakis', 'Slacks', 'Shorts', 'Skirt', 'Dress',
  'Jacket', 'Blazer', 'Sweater', 'Shoes', 'Sneakers', 'Boots', 'Heels', 'Flats',
  'Hat', 'Cap', 'Apron', 'Uniform', 'Vest', 'Mask', 'Safety vest', 'Chef\'s jacket', 'Reflective vest', 'Uniform top', 'Full attire',
];

const COLORS = [
  'Black', 'White', 'Navy', 'Gray', 'Brown', 'Beige', 'Red', 'Blue', 'Green', 'Yellow', 'Orange', 'Pink', 'Purple', 'Maroon', 'Burgundy', 'Tan', 'Khaki',
];

const ATTIRE_RESTRICTIONS_ALL = [
  'No rips, holes in clothes', 'No open toed shoes', 'No tank tops', 'No jeans', 'No crop tops', 'No shorts', 'No baggy clothing', 'No hoodies', 'No headphones', 'Clear bags only',
  'No revealing clothing', 'No crocs, flip flops', 'No graphics on clothes', 'No tennis shoes, sneakers', 'No logos or text on clothes', 'No leggings', 'No jeans with rips', 'No hats', 'No high heel shoes',
];
const ATTIRE_RESTRICTIONS_BASIC = ATTIRE_RESTRICTIONS_ALL.slice(0, 10);

const GROOMING_ELEVATED = [
  'No piercings on the face', 'No facial tattoos', 'No acrylics or fake nails', 'No heavy jewelry (limited jewelry is okay)', 'No extreme hair colors (e.g., blue)', 'No unpleasant hygiene (use deodorant)',
  'No visible tattoos (tattoos must be covered)', 'No long or dirty fingernails', 'No nail color/paints', 'No loose long hair (long hair must be pulled up)', 'No untrimmed beard or mustache', 'No fake eyelashes',
];
const GROOMING_PROFESSIONAL = GROOMING_ELEVATED.slice(0, 8);
const GROOMING_STANDARD = GROOMING_ELEVATED.slice(0, 5);
/** All system grooming rules (for Custom: selectable list) */
const GROOMING_ALL = [...GROOMING_ELEVATED];

function sanitizeJobDutiesPreview(html: string): string {
  return sanitizeHtml(html || '');
}

interface Step6DressProps {
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

function SectionCard({
  title,
  summary,
  onSelect,
  children,
}: {
  title: string;
  summary: React.ReactNode;
  onSelect: () => void;
  children?: React.ReactNode;
}) {
  return (
    <div className="bg-white rounded-2xl border border-slate-200 p-5">
      <div className="flex items-start justify-between gap-4">
        <div className="min-w-0 flex-1">
          <h3 className="font-semibold text-slate-900 mb-1">{title}</h3>
          <div className="text-sm text-slate-600">{summary}</div>
          {children}
        </div>
        <Button type="button" variant="secondary" size="sm" onClick={onSelect}>
          Select
        </Button>
      </div>
    </div>
  );
}

export function Step6Dress({ formData, updateFormData, onNext, onBack, canGoBack = false, errors = {} }: Step6DressProps) {
  const copy = useTranslations(TEXTS);
  const s6 = copy.step6;
  const [openModal, setOpenModal] = useState<'dress-code' | 'dress-items' | 'attire' | 'grooming' | 'job-duties' | 'additional' | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const DRESS_TYPE_LABEL_KEY: Record<JobFormData['selectedDressType'], string> = {
    '': '',
    casual: 'dressCasual',
    'smart-casual': 'dressSmartCasual',
    business: 'dressOffice',
    'dress-provided': 'dressProvided',
    'custom-uniform': 'dressCreateUniform',
    'no-preference': 'dressNoPreference',
  };
  const dressCodeLabel = formData.selectedDressType ? (s6 as Record<string, string>)[DRESS_TYPE_LABEL_KEY[formData.selectedDressType]] || formData.selectedDressType : null;

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (!files?.length) return;
    const names: string[] = [];
    for (let i = 0; i < files.length; i++) {
      const f = files[i];
      if (f.type === 'application/pdf' && f.size <= 10 * 1024 * 1024) names.push(f.name);
    }
    if (names.length) updateFormData({ uploadedFileNames: [...(formData.uploadedFileNames || []), ...names] });
    e.target.value = '';
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    const files = e.dataTransfer.files;
    if (!files?.length) return;
    const names: string[] = [];
    for (let i = 0; i < files.length; i++) {
      const f = files[i];
      if (f.type === 'application/pdf' && f.size <= 10 * 1024 * 1024) names.push(f.name);
    }
    if (names.length) updateFormData({ uploadedFileNames: [...(formData.uploadedFileNames || []), ...names] });
  };

  const removeFile = (name: string) => {
    updateFormData({ uploadedFileNames: (formData.uploadedFileNames || []).filter((n) => n !== name) });
  };

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold text-slate-900 mb-2">{s6.title}</h2>
        <p className="text-sm text-slate-600">{s6.subtitle}</p>
      </div>

      <div className="space-y-4">
        {/* Dress Code */}
        <SectionCard
          title={s6.dressCode}
          summary={dressCodeLabel ? dressCodeLabel : 'No dress code selected'}
          onSelect={() => setOpenModal('dress-code')}
        />

        {/* Dress Items */}
        <SectionCard
          title={s6.dressItems}
          summary={
            formData.selectedDressItems?.length
              ? `${formData.selectedDressItems.length} item(s): ${formData.selectedDressItems.map((i) => `${i.item} (${i.color})`).join(', ')}`
              : 'No items selected'
          }
          onSelect={() => setOpenModal('dress-items')}
        />

        {/* Attire Restrictions */}
        <SectionCard
          title={s6.attireRestrictions}
          summary={
            formData.attireRestrictionLevel === 'no-preference'
              ? s6.noPreference
              : formData.attireRestrictionLevel === 'basic'
                ? s6.attireBasic
                : formData.attireRestrictionLevel === 'strict'
                  ? s6.attireStrict
                  : formData.attireRestrictionLevel === 'custom'
                    ? `${(formData.selectedAttireRestrictions || []).length} custom restriction(s)`
                    : 'Not selected'
          }
          onSelect={() => setOpenModal('attire')}
        >
          {(formData.selectedAttireRestrictions?.length ?? 0) > 0 && formData.attireRestrictionLevel && formData.attireRestrictionLevel !== 'no-preference' && (
            <div className="mt-3 flex flex-wrap gap-1.5">
              {(formData.selectedAttireRestrictions || []).map((r) => (
                <span key={r} className="inline-flex items-center rounded-full bg-slate-100 px-2.5 py-0.5 text-xs text-slate-700">
                  {r}
                </span>
              ))}
            </div>
          )}
        </SectionCard>

        {/* Grooming Instructions */}
        <SectionCard
          title={s6.groomingInstructions}
          summary={
            formData.groomingLevel === 'no-preference'
              ? s6.noPreference
              : formData.groomingLevel === 'elevated'
                ? s6.groomingElevated
                : formData.groomingLevel === 'professional'
                  ? s6.groomingProfessional
                  : formData.groomingLevel === 'standard'
                    ? s6.groomingStandard
                    : formData.groomingLevel === 'custom'
                      ? `${(formData.selectedGroomingRestrictions || []).length} grooming rule(s)`
                      : 'Not selected'
          }
          onSelect={() => setOpenModal('grooming')}
        >
          {(formData.selectedGroomingRestrictions?.length ?? 0) > 0 && formData.groomingLevel === 'custom' && (
            <div className="mt-3 flex flex-wrap gap-1.5">
              {(formData.selectedGroomingRestrictions || []).map((r) => (
                <span key={r} className="inline-flex items-center rounded-full bg-slate-100 px-2.5 py-0.5 text-xs text-slate-700">
                  {r}
                </span>
              ))}
            </div>
          )}
        </SectionCard>

        {/* Job Duties */}
        <SectionCard
          title={s6.jobDuties}
          summary={formData.jobDutiesHtml ? `${(formData.jobDutiesHtml.replace(/<[^>]+>/g, '')).trim().slice(0, 60)}…` : 'Not added'}
          onSelect={() => setOpenModal('job-duties')}
        >
          {formData.jobDutiesHtml && (
            <>
              <div
                className="job-duties-preview mt-3 p-3 rounded-xl bg-slate-50 border border-slate-100 text-sm text-slate-700 max-h-32 overflow-y-auto"
                dangerouslySetInnerHTML={{ __html: sanitizeJobDutiesPreview(formData.jobDutiesHtml) }}
              />
              <style>{`
                .job-duties-preview ul { list-style-type: disc !important; list-style-position: outside; padding-left: 1.5rem; margin: 0.35em 0; }
                .job-duties-preview ol { list-style-type: decimal !important; list-style-position: outside; padding-left: 1.5rem; margin: 0.35em 0; }
                .job-duties-preview li { display: list-item !important; margin: 0.125em 0; }
                .job-duties-preview strong, .job-duties-preview b { font-weight: 700; }
                .job-duties-preview em, .job-duties-preview i { font-style: italic; }
                .job-duties-preview u { text-decoration: underline; }
                .job-duties-preview h1 { font-size: 1rem; font-weight: 700; margin: 0.5em 0; }
                .job-duties-preview h2 { font-size: 0.95rem; font-weight: 600; margin: 0.4em 0; }
                .job-duties-preview h3 { font-size: 0.9rem; font-weight: 600; margin: 0.35em 0; }
                .job-duties-preview h4 { font-size: 0.85rem; font-weight: 600; margin: 0.3em 0; }
                .job-duties-preview p { margin: 0.25em 0; }
              `}</style>
            </>
          )}
        </SectionCard>

        {/* Upload Files */}
        <div className="bg-white rounded-2xl border border-slate-200 p-5">
          <h3 className="font-semibold text-slate-900 mb-1">{s6.uploadFiles}</h3>
          <p className="text-sm text-slate-600 mb-2">{s6.uploadSubtitle}</p>
          <input
            ref={fileInputRef}
            type="file"
            accept=".pdf,application/pdf"
            multiple
            className="hidden"
            onChange={handleFileChange}
          />
          <div
            className="border-2 border-dashed border-blue-200 rounded-xl p-8 text-center cursor-pointer hover:bg-slate-50 transition-colors"
            onClick={() => fileInputRef.current?.click()}
            onDragOver={(e) => e.preventDefault()}
            onDrop={handleDrop}
          >
            <span className="text-slate-500">📎 {s6.uploadPlaceholder}</span>
          </div>
          <p className="text-xs text-slate-500 mt-2">{s6.uploadNote}</p>
          {(formData.uploadedFileNames || []).length > 0 && (
            <ul className="mt-2 space-y-1">
              {(formData.uploadedFileNames || []).map((name) => (
                <li key={name} className="flex items-center justify-between text-sm bg-slate-50 rounded px-2 py-1">
                  <span className="truncate">{name}</span>
                  <button type="button" className="text-red-600 hover:underline" onClick={() => removeFile(name)}>Remove</button>
                </li>
              ))}
            </ul>
          )}
        </div>

        {/* Additional Instructions */}
        <SectionCard
          title={s6.additionalInstructions}
          summary={formData.additionalInstructionsHtml ? `${(formData.additionalInstructionsHtml.replace(/<[^>]+>/g, '')).slice(0, 60)}…` : 'Not added'}
          onSelect={() => setOpenModal('additional')}
        />
      </div>

      {errors.selectedDressType && (
        <div className="bg-red-50 border border-red-200 rounded-xl p-3">
          <FieldError message={errors.selectedDressType} />
        </div>
      )}

      {(onNext || onBack) && (
        <div className="bg-white rounded-2xl border border-slate-200 p-6 flex justify-between items-center">
          <Button variant="secondary" onClick={onBack} disabled={!canGoBack}>{s6.back}</Button>
          <Button onClick={onNext}>{s6.next}</Button>
        </div>
      )}

      {/* Modals */}
      <Modal title={s6.dressCodeModalTitle} isOpen={openModal === 'dress-code'} onClose={() => setOpenModal(null)} size="2xl">
        <div className="grid grid-cols-2 sm:grid-cols-3 gap-4">
          {DRESS_CODE_OPTIONS.map((opt) => {
            const nameKey = opt.id === 'casual' ? 'dressCasual' : opt.id === 'smart-casual' ? 'dressSmartCasual' : opt.id === 'business' ? 'dressOffice' : opt.id === 'dress-provided' ? 'dressProvided' : opt.id === 'custom-uniform' ? 'dressCreateUniform' : 'dressNoPreference';
            const descKey = opt.id === 'casual' ? 'dressCasualDesc' : opt.id === 'smart-casual' ? 'dressSmartCasualDesc' : opt.id === 'business' ? 'dressOfficeDesc' : opt.id === 'dress-provided' ? 'dressProvidedDesc' : opt.id === 'custom-uniform' ? 'dressCreateUniformDesc' : '';
            const name = (s6 as Record<string, string>)[nameKey] || opt.id;
            const desc = descKey ? (s6 as Record<string, string>)[descKey] : '';
            return (
              <button
                key={opt.id}
                type="button"
                onClick={() => {
                  updateFormData({ selectedDressType: opt.id });
                  setOpenModal(null);
                }}
                className={`relative border-2 rounded-xl p-4 text-left transition-all ${formData.selectedDressType === opt.id ? 'border-blue-500 bg-blue-50' : 'border-slate-200 hover:border-slate-300'}`}
              >
                {formData.selectedDressType === opt.id && (
                  <span className="absolute top-2 right-2 w-5 h-5 bg-green-500 rounded-full flex items-center justify-center text-white text-xs">✓</span>
                )}
                {opt.image ? (
                  <img src={opt.image} alt={name} className="w-full h-28 object-cover rounded-lg mb-2" onError={(e) => { e.currentTarget.style.display = 'none'; }} />
                ) : opt.id === 'no-preference' ? (
                  <div className="w-full h-28 bg-slate-50 rounded-lg mb-2 flex items-center justify-center border border-dashed border-slate-200">
                    <Minus className="w-12 h-12 text-slate-400" strokeWidth={2} />
                  </div>
                ) : (
                  <div className="w-full h-28 bg-slate-100 rounded-lg mb-2 flex items-center justify-center">
                    <span className="text-4xl">👔</span>
                  </div>
                )}
                <div className="font-semibold text-slate-900">{name}</div>
                {desc && <div className="text-xs text-slate-600 mt-0.5">{desc}</div>}
              </button>
            );
          })}
        </div>
        <div className="mt-4 flex justify-end">
          <Button onClick={() => setOpenModal(null)}>{s6.done}</Button>
        </div>
      </Modal>

      <Modal title={s6.dressItemsModalTitle} isOpen={openModal === 'dress-items'} onClose={() => setOpenModal(null)} size="lg">
        <DressItemsContent
          items={formData.selectedDressItems || []}
          onUpdate={(items) => updateFormData({ selectedDressItems: items })}
          onDone={() => setOpenModal(null)}
          s6={s6}
        />
      </Modal>

      <Modal title={s6.attireModalTitle} isOpen={openModal === 'attire'} onClose={() => setOpenModal(null)} size="2xl">
        <AttireRestrictionsContent
          level={formData.attireRestrictionLevel}
          restrictions={formData.selectedAttireRestrictions || []}
          onUpdate={(level, restrictions) => {
            updateFormData({ attireRestrictionLevel: level, selectedAttireRestrictions: restrictions });
          }}
          onDone={() => setOpenModal(null)}
          s6={s6}
        />
      </Modal>

      <Modal title={s6.groomingModalTitle} isOpen={openModal === 'grooming'} onClose={() => setOpenModal(null)} size="2xl">
        <GroomingContent
          level={formData.groomingLevel}
          restrictions={formData.selectedGroomingRestrictions || []}
          onUpdate={(level, nextRestrictions) =>
            updateFormData({
              groomingLevel: level,
              selectedGroomingRestrictions: nextRestrictions != null ? nextRestrictions : (formData.selectedGroomingRestrictions ?? []),
            })
          }
          onDone={() => setOpenModal(null)}
          s6={s6}
        />
      </Modal>

      <Modal title={s6.jobDutiesModalTitle} isOpen={openModal === 'job-duties'} onClose={() => setOpenModal(null)} size="lg">
        <div className="space-y-3">
          <p className="text-sm text-slate-600">{s6.jobDutiesSubtitle}</p>
          <SimpleRichEditor
            value={formData.jobDutiesHtml}
            onChange={(html) => updateFormData({ jobDutiesHtml: html })}
            placeholder="• Task one&#10;• Task two&#10;• Task three"
            minHeight="220px"
          />
          <div className="flex justify-end">
            <Button onClick={() => setOpenModal(null)}>{s6.done}</Button>
          </div>
        </div>
      </Modal>

      <Modal title={s6.additionalModalTitle} isOpen={openModal === 'additional'} onClose={() => setOpenModal(null)} size="lg">
        <div className="space-y-2">
          <textarea
            className="w-full rounded-xl border border-slate-200 px-3 py-2 text-sm min-h-[200px] focus:ring-2 focus:ring-blue-500 focus:outline-none"
            value={formData.additionalInstructionsHtml}
            onChange={(e) => updateFormData({ additionalInstructionsHtml: e.target.value })}
            placeholder="Any additional instructions..."
          />
          <Button onClick={() => setOpenModal(null)}>{s6.done}</Button>
        </div>
      </Modal>
    </div>
  );
}

function DressItemsContent({
  items,
  onUpdate,
  onDone,
  s6,
}: {
  items: Array<{ item: string; color: string; id: string }>;
  onUpdate: (items: Array<{ item: string; color: string; id: string }>) => void;
  onDone: () => void;
  s6: Record<string, string>;
}) {
  const [item, setItem] = useState('');
  const [color, setColor] = useState('');

  const add = () => {
    if (!item || !color) return;
    onUpdate([...items, { item, color, id: `${Date.now()}-${Math.random().toString(36).slice(2)}` }]);
    setItem('');
    setColor('');
  };

  const remove = (id: string) => onUpdate(items.filter((i) => i.id !== id));

  return (
    <div className="space-y-4">
      <div className="grid grid-cols-2 gap-3">
        <div>
          <label className="block text-xs text-slate-500 mb-1">{s6.searchAttire}</label>
          <select className="w-full rounded-xl border border-slate-200 px-3 py-2 text-sm" value={item} onChange={(e) => setItem(e.target.value)}>
            <option value="">Select item</option>
            {DRESS_ITEMS.map((i) => (
              <option key={i} value={i}>{i}</option>
            ))}
          </select>
        </div>
        <div>
          <label className="block text-xs text-slate-500 mb-1">Color</label>
          <select className="w-full rounded-xl border border-slate-200 px-3 py-2 text-sm" value={color} onChange={(e) => setColor(e.target.value)}>
            <option value="">Select color</option>
            {COLORS.map((c) => (
              <option key={c} value={c}>{c}</option>
            ))}
          </select>
        </div>
      </div>
      <Button type="button" size="sm" onClick={add} disabled={!item || !color}>{s6.addItem}</Button>
      {items.length > 0 && (
        <div className="flex flex-wrap gap-2">
          {items.map((i) => (
            <span key={i.id} className="inline-flex items-center gap-1 rounded-full bg-slate-100 px-3 py-1 text-sm">
              {i.item} ({i.color})
              <button type="button" className="text-slate-500 hover:text-red-600" onClick={() => remove(i.id)}>×</button>
            </span>
          ))}
        </div>
      )}
      <div className="flex justify-end pt-2">
        <Button onClick={onDone}>{s6.done}</Button>
      </div>
    </div>
  );
}

function AttireRestrictionsContent({
  level,
  restrictions,
  onUpdate,
  onDone,
  s6,
}: {
  level: string;
  restrictions: string[];
  onUpdate: (level: '' | 'basic' | 'strict' | 'custom' | 'no-preference', restrictions: string[]) => void;
  onDone: () => void;
  s6: Record<string, string>;
}) {
  const [customInput, setCustomInput] = useState('');
  const levels: Array<'' | 'basic' | 'strict' | 'custom' | 'no-preference'> = ['basic', 'strict', 'custom', 'no-preference'];
  const list = level === 'strict' ? ATTIRE_RESTRICTIONS_ALL : level === 'basic' ? ATTIRE_RESTRICTIONS_BASIC : restrictions;
  const isPreset = level === 'basic' || level === 'strict';

  const addCustomRestriction = () => {
    const text = customInput.trim();
    if (!text) return;
    if (restrictions.includes(text)) {
      setCustomInput('');
      return;
    }
    onUpdate('custom', [...restrictions, text]);
    setCustomInput('');
  };

  const removeCustomRestriction = (item: string) => {
    onUpdate('custom', restrictions.filter((r) => r !== item));
  };

  return (
    <div className="space-y-4">
      <div className="flex flex-wrap gap-2">
        {levels.map((l) => (
          <button
            key={l || 'none'}
            type="button"
            onClick={() => {
              if (l === 'basic') onUpdate('basic', ATTIRE_RESTRICTIONS_BASIC);
              else if (l === 'strict') onUpdate('strict', ATTIRE_RESTRICTIONS_ALL);
              else if (l === 'no-preference') onUpdate('no-preference', []);
              else onUpdate('custom', restrictions);
            }}
            className={`px-4 py-2 rounded-xl border-2 text-sm font-medium ${level === l ? 'border-blue-500 bg-blue-50 text-blue-800' : 'border-slate-200 hover:border-slate-300'}`}
          >
            {l === 'no-preference' ? s6.noPreference : l === 'basic' ? s6.attireBasic : l === 'strict' ? s6.attireStrict : s6.attireCustom}
          </button>
        ))}
      </div>
      {level && level !== 'no-preference' && (
        <div className="grid grid-cols-1 gap-4 text-sm text-slate-700">
          {isPreset ? (
            <div className="grid grid-cols-2 gap-2">
              {list.map((r) => (
                <div key={r} className="flex items-start gap-2">• {r}</div>
              ))}
            </div>
          ) : (
            <div className="space-y-2">
              <p className="text-slate-600 mb-1">{s6.attireCustomHint ?? 'Add custom restrictions (type and add each one):'}</p>
              <div className="flex gap-2">
                <input
                  type="text"
                  className="flex-1 rounded-xl border border-slate-200 px-3 py-2 text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none"
                  value={customInput}
                  onChange={(e) => setCustomInput(e.target.value)}
                  onKeyDown={(e) => e.key === 'Enter' && (e.preventDefault(), addCustomRestriction())}
                  placeholder={s6.attireCustomPlaceholder ?? 'e.g. No jeans, No open shoes'}
                />
                <Button type="button" size="sm" onClick={addCustomRestriction} disabled={!customInput.trim()}>
                  {s6.addItem}
                </Button>
              </div>
              {restrictions.length > 0 && (
                <div className="flex flex-wrap gap-1.5 mt-2">
                  {restrictions.map((r) => (
                    <span
                      key={r}
                      className="inline-flex items-center gap-1 rounded-full bg-slate-100 px-3 py-1 text-sm text-slate-700"
                    >
                      {r}
                      <button
                        type="button"
                        className="text-slate-500 hover:text-red-600 rounded p-0.5"
                        onClick={() => removeCustomRestriction(r)}
                        aria-label={`Remove ${r}`}
                      >
                        <X className="w-3.5 h-3.5" />
                      </button>
                    </span>
                  ))}
                </div>
              )}
            </div>
          )}
        </div>
      )}
      <div className="flex justify-end pt-2">
        <Button onClick={onDone}>{s6.done}</Button>
      </div>
    </div>
  );
}

function GroomingContent({
  level,
  restrictions,
  onUpdate,
  onDone,
  s6,
}: {
  level: string;
  restrictions: string[];
  onUpdate: (level: '' | 'elevated' | 'professional' | 'standard' | 'custom' | 'no-preference', restrictions: string[]) => void;
  onDone: () => void;
  s6: Record<string, string>;
}) {
  const [customInput, setCustomInput] = useState('');
  const levels: Array<'' | 'elevated' | 'professional' | 'standard' | 'custom' | 'no-preference'> = ['elevated', 'professional', 'standard', 'custom', 'no-preference'];
  const rules = level === 'elevated' ? GROOMING_ELEVATED : level === 'professional' ? GROOMING_PROFESSIONAL : level === 'standard' ? GROOMING_STANDARD : [];
  const isPreset = level === 'elevated' || level === 'professional' || level === 'standard';
  const customOnlyItems = restrictions.filter((r) => !GROOMING_ALL.includes(r));

  const toggleSystemRule = (rule: string) => {
    const next = restrictions.includes(rule) ? restrictions.filter((r) => r !== rule) : [...restrictions, rule];
    onUpdate('custom', next);
  };

  const addCustomItem = () => {
    const text = customInput.trim();
    if (!text) return;
    if (restrictions.includes(text)) {
      setCustomInput('');
      return;
    }
    onUpdate('custom', [...restrictions, text]);
    setCustomInput('');
  };

  const removeItem = (item: string) => {
    onUpdate('custom', restrictions.filter((r) => r !== item));
  };

  return (
    <div className="space-y-4">
      <div className="flex flex-wrap gap-2">
        {levels.map((l) => (
          <button
            key={l || 'none'}
            type="button"
            onClick={() => {
              if (l === 'no-preference') onUpdate('no-preference', []);
              else if (l === 'custom') onUpdate('custom', restrictions);
              else onUpdate(l, []);
            }}
            className={`px-4 py-2 rounded-xl border-2 text-sm font-medium ${level === l ? 'border-blue-500 bg-blue-50 text-blue-800' : 'border-slate-200 hover:border-slate-300'}`}
          >
            {l === 'no-preference' ? s6.noPreference : l === 'elevated' ? s6.groomingElevated : l === 'professional' ? s6.groomingProfessional : l === 'standard' ? s6.groomingStandard : s6.groomingCustom}
          </button>
        ))}
      </div>
      {level && level !== 'no-preference' && (
        <div className="space-y-4 text-sm text-slate-700">
          {isPreset ? (
            <div className="grid grid-cols-2 gap-2">
              {rules.map((r) => (
                <div key={r} className="flex items-start gap-2">• {r}</div>
              ))}
            </div>
          ) : (
            <div className="space-y-4">
              <div>
                <p className="text-slate-600 font-medium mb-2">{s6.groomingSystemLabel ?? 'Select from system grooming rules:'}</p>
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 max-h-48 overflow-y-auto border border-slate-200 rounded-xl p-3 bg-slate-50/50">
                  {GROOMING_ALL.map((r) => (
                    <label key={r} className="flex items-center gap-2 py-1 cursor-pointer hover:bg-white rounded px-2">
                      <input
                        type="checkbox"
                        checked={restrictions.includes(r)}
                        onChange={() => toggleSystemRule(r)}
                        className="rounded border-slate-300 text-blue-600"
                      />
                      <span className="text-slate-700">{r}</span>
                    </label>
                  ))}
                </div>
              </div>
              <div>
<p className="text-slate-600 font-medium mb-1">{s6.groomingCustomHint ?? 'Add your own (type and add each one):'}</p>
                  <div className="flex gap-2">
                  <input
                    type="text"
                    className="flex-1 rounded-xl border border-slate-200 px-3 py-2 text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none"
                    value={customInput}
                    onChange={(e) => setCustomInput(e.target.value)}
                    onKeyDown={(e) => e.key === 'Enter' && (e.preventDefault(), addCustomItem())}
                    placeholder={s6.groomingCustomPlaceholder ?? 'e.g. No visible piercings'}
                  />
                  <Button type="button" size="sm" onClick={addCustomItem} disabled={!customInput.trim()}>
                    {s6.addItem}
                  </Button>
                </div>
                {customOnlyItems.length > 0 && (
                  <div className="flex flex-wrap gap-1.5 mt-2">
                    {customOnlyItems.map((r) => (
                      <span
                        key={r}
                        className="inline-flex items-center gap-1 rounded-full bg-slate-100 px-3 py-1 text-sm text-slate-700"
                      >
                        {r}
                        <button
                          type="button"
                          className="text-slate-500 hover:text-red-600 rounded p-0.5"
                          onClick={() => removeItem(r)}
                          aria-label={`Remove ${r}`}
                        >
                          <X className="w-3.5 h-3.5" />
                        </button>
                      </span>
                    ))}
                  </div>
                )}
              </div>
            </div>
          )}
        </div>
      )}
      <div className="flex justify-end pt-2">
        <Button onClick={onDone}>{s6.done}</Button>
      </div>
    </div>
  );
}
