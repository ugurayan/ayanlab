import React, { useState, useMemo } from 'react';
import { Input } from '../../../ui/components/Input';
import { Button } from '../../../ui/components/Button';
import { Calendar, X, MoreHorizontal, Info, DollarSign, TrendingUp } from 'lucide-react';
import type { JobFormData } from '../types';
import type { StepErrors } from './validation';
import { useJobRoleWages } from '../../../data/jobHooks';
import { getProfileTaxAndFee } from '../../../data/profileUiData';
import { WizardStepHeader } from './WizardSectionHeading';
import { useJobWizardCopy } from '../i18n/wizardTexts';
import { useLanguage } from '../../../state/i18n';

const DAY_IDS = ['sun', 'mon', 'tue', 'wed', 'thu', 'fri', 'sat'];
const BREAK_VALUES = ['0 min', '15 min', '30 min', '45 min', '60 min'] as const;

function countRecurringDates(start: string, end: string, shiftDays: string[]): number {
  if (!start || !end || shiftDays.length === 0) return 0;
  const startDate = new Date(start);
  const endDate = new Date(end);
  let count = 0;
  const d = new Date(startDate);
  while (d <= endDate) {
    if (shiftDays.includes(DAY_IDS[d.getDay()])) count++;
    d.setDate(d.getDate() + 1);
  }
  return count;
}

function formatCurrency(amount: number, currency: string): string {
  if (currency === 'USD') return `$${amount.toFixed(2)}`;
  return `${currency} ${amount.toFixed(2)}`;
}

interface Step3ScheduleProps {
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

export function Step3Schedule({
  formData,
  updateFormData,
  onNext,
  onBack,
  canGoBack = false,
  errors = {},
}: Step3ScheduleProps) {
  const w = useJobWizardCopy();
  const c = w.common;
  const s = w.step3;
  const { language } = useLanguage();
  const shiftDayLabels = DAY_IDS.map((id) => ({ id, label: s.days[id] }));
  const breakOptions = BREAK_VALUES.map((value) => ({
    value,
    label: value === '0 min' ? s.breakNoBreak : value,
  }));
  const dateLocale = language === 'de' ? 'de-DE' : language === 'tr' ? 'tr-TR' : 'en-US';
  const [addDateValue, setAddDateValue] = useState('');

  const zipcode = (formData.workLocationZipcode || '').trim() || '10001';
  const { data: wageStats } = useJobRoleWages(zipcode);

  const scheduleDateCount = formData.recurringSchedule
    ? countRecurringDates(formData.recurringStartDate, formData.recurringEndDate, formData.shiftDays ?? [])
    : formData.selectedDates.length;

  const totalHours = useMemo(() => {
    if (scheduleDateCount === 0) return 0;
    const [sh, sm] = (formData.scheduleStartTime || '0:0').split(':').map(Number);
    const [eh, em] = (formData.scheduleEndTime || '0:0').split(':').map(Number);
    const minsPerDay = (eh - sh) * 60 + (em - sm);
    return (scheduleDateCount * minsPerDay) / 60;
  }, [scheduleDateCount, formData.scheduleStartTime, formData.scheduleEndTime]);

  const persons = Math.max(0, parseInt(formData.employeesNeeded || '0', 10));
  const wage = Math.max(0, parseFloat(formData.wageAmount || '0'));
  const { taxPercent: taxPct, feePercent: feePct } = getProfileTaxAndFee();
  const tipPct = Math.max(0, parseFloat(formData.tipPercent || '0'));
  const tipFixed = Math.max(0, parseFloat(formData.tipAmount || '0'));
  const currency = formData.wageCurrency || 'USD';

  const baseCost = formData.wageType === 'hourly'
    ? persons * wage * totalHours
    : persons * wage * scheduleDateCount;
  const taxAmount = baseCost * (taxPct / 100);
  const fees = baseCost * (feePct / 100);
  const tipAmount = baseCost * (tipPct / 100) + tipFixed;
  const totalCost = baseCost + taxAmount + fees + tipAmount;

  const addDate = () => {
    if (!addDateValue) return;
    const iso = new Date(addDateValue).toISOString().slice(0, 10);
    if (!formData.selectedDates.includes(iso)) {
      updateFormData({ selectedDates: [...formData.selectedDates, iso].sort() });
    }
    setAddDateValue('');
  };

  const removeDate = (date: string) => {
    updateFormData({ selectedDates: formData.selectedDates.filter((d) => d !== date) });
  };

  const toggleShiftDay = (dayId: string) => {
    const current = formData.shiftDays ?? [];
    const next = current.includes(dayId)
      ? current.filter((d) => d !== dayId)
      : [...current, dayId];
    updateFormData({ shiftDays: next });
  };

  const hasErrors = Object.keys(errors).length > 0;

  return (
    <div className="space-y-6">
      <div className="flex items-start justify-between gap-4">
        <WizardStepHeader title={s.title} subtitle={s.subtitle} />
        <label className="flex shrink-0 items-center gap-2 cursor-pointer">
          <span className="text-sm font-medium text-slate-700">{s.recurringSchedule}</span>
          <button
            type="button"
            role="switch"
            aria-checked={formData.recurringSchedule}
            onClick={() => updateFormData({ recurringSchedule: !formData.recurringSchedule })}
            className={`relative inline-flex h-6 w-11 shrink-0 rounded-full border-2 transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 ${
              formData.recurringSchedule ? 'border-blue-500 bg-blue-500' : 'border-slate-200 bg-slate-200'
            }`}
          >
            <span
              className={`pointer-events-none inline-block h-5 w-5 rounded-full bg-white shadow transition-transform ${
                formData.recurringSchedule ? 'translate-x-5' : 'translate-x-0.5'
              }`}
              style={{ marginTop: 2 }}
            />
          </button>
        </label>
      </div>

      {hasErrors && (
        <div className="rounded-xl border border-red-200 bg-red-50 p-4">
          <p className="text-sm font-medium text-red-800">{c.fixErrorsTitle}</p>
          <ul className="mt-2 list-inside list-disc text-sm text-red-700">
            {Object.entries(errors).map(([key, message]) => (
              <li key={key}>{message}</li>
            ))}
          </ul>
        </div>
      )}

      <div className="bg-white rounded-2xl border border-slate-200 p-6 space-y-6">
        <div className="flex items-center justify-between">
          <h3 className="rounded-lg border border-slate-200/60 bg-slate-50/80 px-3 py-2 text-sm font-semibold text-slate-800 shrink-0">
            {s.dateAndTimes}
          </h3>
          <button type="button" className="p-1 rounded hover:bg-slate-100 text-slate-500">
            <MoreHorizontal size={18} />
          </button>
        </div>

        <div>
          <label className="block text-sm font-medium text-slate-700 mb-1">{s.scheduleName}</label>
          <Input
            value={formData.scheduleName}
            onChange={(e) => updateFormData({ scheduleName: e.target.value })}
            placeholder={s.scheduleNamePlaceholder}
          />
        </div>

        {formData.recurringSchedule ? (
          <>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">{s.startDate} *</label>
                <Input
                  type="date"
                  value={formData.recurringStartDate}
                  onChange={(e) => updateFormData({ recurringStartDate: e.target.value })}
                  className={errors.recurringStartDate ? 'border-red-400' : ''}
                />
                <FieldError message={errors.recurringStartDate} />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">{s.endDate} *</label>
                <Input
                  type="date"
                  value={formData.recurringEndDate}
                  onChange={(e) => updateFormData({ recurringEndDate: e.target.value })}
                  className={errors.recurringEndDate ? 'border-red-400' : ''}
                />
                <FieldError message={errors.recurringEndDate} />
              </div>
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-2">{s.shiftDays} *</label>
              <div className="flex flex-wrap gap-2">
                {shiftDayLabels.map(({ id, label }) => (
                  <button
                    key={id}
                    type="button"
                    onClick={() => toggleShiftDay(id)}
                    className={`min-w-[44px] rounded-full px-3 py-2 text-sm font-medium transition-colors ${
                      (formData.shiftDays ?? []).includes(id)
                        ? 'bg-blue-600 text-white'
                        : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                    }`}
                  >
                    {label}
                  </button>
                ))}
              </div>
              <FieldError message={errors.shiftDays} />
            </div>
          </>
        ) : (
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-2">{s.dates} *</label>
            <div className="flex flex-wrap gap-2 mb-2">
              {formData.selectedDates.map((date) => (
                <div
                  key={date}
                  className="inline-flex items-center gap-2 px-3 py-1.5 bg-blue-50 text-blue-700 rounded-full text-sm"
                >
                  <span>
                    {new Date(date + 'T12:00:00').toLocaleDateString(dateLocale, {
                      weekday: 'short',
                      month: 'short',
                      day: 'numeric',
                    })}
                  </span>
                  <button
                    type="button"
                    onClick={() => removeDate(date)}
                    className="hover:bg-blue-100 rounded-full p-0.5"
                  >
                    <X size={14} />
                  </button>
                </div>
              ))}
            </div>
            <div className="flex gap-2">
              <Input
                type="date"
                value={addDateValue}
                onChange={(e) => setAddDateValue(e.target.value)}
                className="max-w-[180px]"
              />
              <Button type="button" variant="secondary" onClick={addDate} disabled={!addDateValue}>
                <Calendar size={16} className="mr-2" />
                {s.addDates}
              </Button>
            </div>
            <FieldError message={errors.selectedDates} />
          </div>
        )}

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">{s.startTime} *</label>
            <Input
              type="time"
              value={formData.scheduleStartTime}
              onChange={(e) => updateFormData({ scheduleStartTime: e.target.value })}
              className={errors.scheduleStartTime ? 'border-red-400' : ''}
            />
            <FieldError message={errors.scheduleStartTime} />
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">{s.endTime} *</label>
            <Input
              type="time"
              value={formData.scheduleEndTime}
              onChange={(e) => updateFormData({ scheduleEndTime: e.target.value })}
              className={errors.scheduleEndTime ? 'border-red-400' : ''}
            />
            <FieldError message={errors.scheduleEndTime} />
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">{s.unpaidBreak} *</label>
            <select
              className={`w-full rounded-xl border bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 ${
                errors.totalUnpaidBreak ? 'border-red-400 focus:ring-red-500' : 'border-slate-200 focus:ring-blue-500'
              }`}
              value={formData.totalUnpaidBreak}
              onChange={(e) => updateFormData({ totalUnpaidBreak: e.target.value })}
            >
              {breakOptions.map((opt) => (
                <option key={opt.value} value={opt.value}>
                  {opt.label}
                </option>
              ))}
            </select>
            <FieldError message={errors.totalUnpaidBreak} />
          </div>
        </div>

        <p className="text-xs text-slate-500">
          {s.expectedBreaks}{' '}
          {formData.totalUnpaidBreak === '0 min' ? s.expectedBreaksNone : formData.totalUnpaidBreak}
        </p>

        <label className="flex items-center gap-2 cursor-pointer">
          <input
            type="checkbox"
            checked={formData.preferSamePros}
            onChange={(e) => updateFormData({ preferSamePros: e.target.checked })}
            className="rounded border-slate-300"
          />
          <span className="text-sm text-slate-700">{s.preferSamePros}</span>
        </label>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 pt-2">
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">{s.employeesNeeded} *</label>
            <Input
              type="number"
              value={formData.employeesNeeded}
              onChange={(e) => updateFormData({ employeesNeeded: e.target.value })}
              placeholder={s.employeesPlaceholder}
              min={1}
              className={errors.employeesNeeded ? 'border-red-400' : ''}
            />
            <FieldError message={errors.employeesNeeded} />
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">{s.wageAmount} *</label>
            <div className="flex gap-2 items-center">
              <span className="text-slate-600 font-medium shrink-0">{currency === 'USD' ? '$' : currency}</span>
              <select
                className="w-24 rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                value={formData.wageType}
                onChange={(e) => updateFormData({ wageType: e.target.value as 'hourly' | 'daily' })}
              >
                <option value="hourly">{s.wageHourly}</option>
                <option value="daily">{s.wageDaily}</option>
              </select>
              <Input
                type="number"
                value={formData.wageAmount}
                onChange={(e) => updateFormData({ wageAmount: e.target.value })}
                placeholder={s.wagePlaceholder}
                min={0}
                step={0.01}
                className={`flex-1 ${errors.wageAmount ? 'border-red-400' : ''}`}
              />
            </div>
            <FieldError message={errors.wageAmount} />
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">{s.tipPercent}</label>
            <Input
              type="number"
              value={formData.tipPercent}
              onChange={(e) => updateFormData({ tipPercent: e.target.value })}
              placeholder={s.tipPercentPlaceholder}
              min={0}
              max={100}
              step={1}
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">{s.tipFixed}</label>
            <div className="flex items-center gap-2">
              <span className="text-slate-600 font-medium shrink-0">{currency === 'USD' ? '$' : currency}</span>
              <Input
                type="number"
                value={formData.tipAmount}
                onChange={(e) => updateFormData({ tipAmount: e.target.value })}
                placeholder="0"
                min={0}
                step={0.01}
                className="flex-1"
              />
            </div>
          </div>
        </div>

        {wageStats && (
          <div className="rounded-xl border border-slate-200 bg-slate-50 p-4">
            <h4 className="text-sm font-semibold text-slate-800 flex items-center gap-2 mb-2">
              <TrendingUp size={16} />
              Wage guide for your area (zip {zipcode})
            </h4>
            <p className="text-xs text-slate-600 mb-3">Typical hourly range for this job profile in your location.</p>
            <div className="flex flex-wrap gap-4 text-sm">
              <span><strong>Min:</strong> {formatCurrency(wageStats.minHourly, wageStats.currency)}/hr</span>
              <span><strong>Avg:</strong> {formatCurrency(wageStats.avgHourly, wageStats.currency)}/hr</span>
              <span><strong>Max:</strong> {formatCurrency(wageStats.maxHourly, wageStats.currency)}/hr</span>
            </div>
          </div>
        )}

        <div className="rounded-xl border border-blue-200 bg-blue-50/50 p-4">
          <h4 className="text-sm font-semibold text-slate-800 flex items-center gap-2 mb-2">
            <DollarSign size={16} />
            Estimated total cost
          </h4>
          <p className="text-xs text-slate-600 mb-3 flex items-start gap-1">
            <Info size={14} className="shrink-0 mt-0.5" />
            <span><strong>How we calculate:</strong> Base = # of persons × wage × {formData.wageType === 'hourly' ? 'total hours' : 'number of dates'}. Tax and fees use your profile rates ({taxPct}% tax, {feePct}% fee). Optional tip can be % of base and/or fixed amount.</span>
          </p>
          <div className="space-y-1 text-sm">
            <div className="flex justify-between">
              <span className="text-slate-600">Base ({persons} × {wage} × {formData.wageType === 'hourly' ? `${totalHours.toFixed(1)} hrs` : `${scheduleDateCount} date(s)`})</span>
              <span className="font-medium">{formatCurrency(baseCost, currency)}</span>
            </div>
            {taxPct > 0 && (
              <div className="flex justify-between">
                <span className="text-slate-600">Tax ({taxPct}%)</span>
                <span className="font-medium">{formatCurrency(taxAmount, currency)}</span>
              </div>
            )}
            {fees > 0 && (
              <div className="flex justify-between">
                <span className="text-slate-600">Fees ({feePct}%)</span>
                <span className="font-medium">{formatCurrency(fees, currency)}</span>
              </div>
            )}
            {tipAmount > 0 && (
              <div className="flex justify-between">
                <span className="text-slate-600">Tip{tipPct > 0 && tipFixed > 0 ? ` (${tipPct}% + fixed)` : tipPct > 0 ? ` (${tipPct}%)` : ' (fixed)'}</span>
                <span className="font-medium">{formatCurrency(tipAmount, currency)}</span>
              </div>
            )}
            <div className="flex justify-between pt-2 border-t border-blue-200 font-semibold text-slate-900">
              <span>Total</span>
              <span>{formatCurrency(totalCost, currency)}</span>
            </div>
          </div>
        </div>

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
