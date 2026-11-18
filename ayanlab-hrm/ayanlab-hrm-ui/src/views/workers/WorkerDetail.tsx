import React, { useEffect, useMemo, useState } from 'react';
import { useForm } from 'react-hook-form';
import { Modal } from '../../ui/components/Modal';
import { Button } from '../../ui/components/Button';
import { Badge } from '../../ui/components/Badge';
import { useUpdateWorker } from '../../data/hooks';
import type { Worker, WorkerIncident, WorkerIncidentSeverity, WorkerIncidentType } from '../../mock/mockDb';

type WorkerDetailTab = 'overview' | 'incidents' | 'punctuality' | 'earnings';

export interface WorkerDetailProps {
  worker: Worker;
  isOpen: boolean;
  onClose: () => void;
  initialTab?: WorkerDetailTab;
}

type WorkerFormData = {
  displayName: string;
  jobProfiles: string;
  skills: string;
  rating: number;
  ratingCount: number;
  reliability: number;
  acceptanceRate: number;
  onTimeStarts: number;
  lateStarts: number;
  jobsCompleted: number;
  jobsNotCompleted: number;
  noShows: number;
  criticalIncidents: number;
  reliabilityIncidents: number;
  avgHourlyRateCents: number;
  currency: string;
  lastMonthCents: number;
  last3MonthsCents: number;
  last6MonthsCents: number;
  last12MonthsCents: number;
  notes: string;
};

function toPercent(n: number) {
  return `${Math.round((n ?? 0) * 100)}%`;
}

function money(cents: number, currency: string) {
  const value = (cents ?? 0) / 100;
  return new Intl.NumberFormat(undefined, { style: 'currency', currency }).format(value);
}

function clamp01(n: number) {
  if (Number.isNaN(n)) return 0;
  return Math.max(0, Math.min(1, n));
}

export function WorkerDetail({ worker, isOpen, onClose, initialTab = 'overview' }: WorkerDetailProps) {
  const updateMutation = useUpdateWorker();
  const [tab, setTab] = useState<WorkerDetailTab>(initialTab);
  const [newIncidentType, setNewIncidentType] = useState<WorkerIncidentType>('late_start');
  const [newIncidentSeverity, setNewIncidentSeverity] = useState<WorkerIncidentSeverity>('low');
  const [newIncidentSummary, setNewIncidentSummary] = useState('');

  const defaultValues = useMemo<WorkerFormData>(() => {
    return {
      displayName: worker.displayName ?? '',
      jobProfiles: (worker.jobProfiles ?? []).join(', '),
      skills: (worker.skills ?? []).join(', '),
      rating: worker.rating ?? 0,
      ratingCount: worker.ratingCount ?? 0,
      reliability: worker.reliability ?? 0,
      acceptanceRate: worker.acceptanceRate ?? 0,
      onTimeStarts: worker.onTimeStarts ?? 0,
      lateStarts: worker.lateStarts ?? 0,
      jobsCompleted: worker.jobsCompleted ?? 0,
      jobsNotCompleted: worker.jobsNotCompleted ?? 0,
      noShows: worker.noShows ?? 0,
      criticalIncidents: worker.criticalIncidents ?? 0,
      reliabilityIncidents: worker.reliabilityIncidents ?? 0,
      avgHourlyRateCents: worker.avgHourlyRateCents ?? 0,
      currency: worker.earnings?.currency ?? 'CHF',
      lastMonthCents: worker.earnings?.lastMonthCents ?? 0,
      last3MonthsCents: worker.earnings?.last3MonthsCents ?? 0,
      last6MonthsCents: worker.earnings?.last6MonthsCents ?? 0,
      last12MonthsCents: worker.earnings?.last12MonthsCents ?? 0,
      notes: worker.notes ?? '',
    };
  }, [worker]);

  const { register, handleSubmit, reset, watch, formState: { errors } } = useForm<WorkerFormData>({
    defaultValues,
  });

  useEffect(() => {
    if (isOpen) {
      reset(defaultValues);
      setTab(initialTab);
      setNewIncidentSummary('');
      setNewIncidentType('late_start');
      setNewIncidentSeverity('low');
    }
  }, [isOpen, reset, defaultValues, initialTab]);

  const currency = watch('currency');

  const onSubmit = async (data: WorkerFormData) => {
    const patch: Partial<Worker> = {
      displayName: data.displayName.trim(),
      jobProfiles: data.jobProfiles
        .split(',')
        .map((s) => s.trim())
        .filter(Boolean),
      skills: data.skills
        .split(',')
        .map((s) => s.trim())
        .filter(Boolean),
      rating: Math.max(0, Math.min(5, Number(data.rating) || 0)),
      ratingCount: Math.max(0, Math.floor(Number(data.ratingCount) || 0)),
      reliability: clamp01(Number(data.reliability) || 0),
      acceptanceRate: clamp01(Number(data.acceptanceRate) || 0),
      onTimeStarts: Math.max(0, Math.floor(Number(data.onTimeStarts) || 0)),
      lateStarts: Math.max(0, Math.floor(Number(data.lateStarts) || 0)),
      jobsCompleted: Math.max(0, Math.floor(Number(data.jobsCompleted) || 0)),
      jobsNotCompleted: Math.max(0, Math.floor(Number(data.jobsNotCompleted) || 0)),
      noShows: Math.max(0, Math.floor(Number(data.noShows) || 0)),
      criticalIncidents: Math.max(0, Math.floor(Number(data.criticalIncidents) || 0)),
      reliabilityIncidents: Math.max(0, Math.floor(Number(data.reliabilityIncidents) || 0)),
      avgHourlyRateCents: Math.max(0, Math.floor(Number(data.avgHourlyRateCents) || 0)),
      earnings: {
        currency: data.currency || 'CHF',
        lastMonthCents: Math.max(0, Math.floor(Number(data.lastMonthCents) || 0)),
        last3MonthsCents: Math.max(0, Math.floor(Number(data.last3MonthsCents) || 0)),
        last6MonthsCents: Math.max(0, Math.floor(Number(data.last6MonthsCents) || 0)),
        last12MonthsCents: Math.max(0, Math.floor(Number(data.last12MonthsCents) || 0)),
      },
      notes: data.notes,
    };

    try {
      await updateMutation.mutateAsync({ workerId: worker.id, patch });
      onClose();
    } catch (e: any) {
      console.error('Update worker failed', e);
      alert(e?.message || 'Failed to update worker');
    }
  };

  const addIncident = async () => {
    const summary = newIncidentSummary.trim();
    if (!summary) return;

    const incident: WorkerIncident = {
      id: `inc_${Math.random().toString(16).slice(2, 10)}`,
      workerId: worker.id,
      type: newIncidentType,
      severity: newIncidentSeverity,
      occurredAt: new Date().toISOString(),
      summary,
    };

    const nextIncidents = [incident, ...(worker.lastIncidents ?? [])].slice(0, 20);
    const patch: Partial<Worker> = {
      lastIncidents: nextIncidents,
      criticalIncidents:
        worker.criticalIncidents + (incident.severity === 'critical' ? 1 : 0),
      reliabilityIncidents:
        worker.reliabilityIncidents + (incident.type === 'late_start' || incident.type === 'no_show' ? 1 : 0),
      lateStarts: worker.lateStarts + (incident.type === 'late_start' ? 1 : 0),
      noShows: worker.noShows + (incident.type === 'no_show' ? 1 : 0),
    };

    try {
      await updateMutation.mutateAsync({ workerId: worker.id, patch });
      setNewIncidentSummary('');
    } catch (e: any) {
      console.error('Add incident failed', e);
      alert(e?.message || 'Failed to add incident');
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={`Worker details — ${worker.displayName}`} size="2xl">
      <div className="space-y-4">
        <div className="flex flex-wrap items-center gap-2">
          <button
            type="button"
            className={`px-3 py-1.5 rounded-full text-sm border ${tab === 'overview' ? 'bg-slate-900 text-white border-slate-900' : 'bg-white text-slate-700 border-slate-200 hover:bg-slate-50'}`}
            onClick={() => setTab('overview')}
          >
            Overview
          </button>
          <button
            type="button"
            className={`px-3 py-1.5 rounded-full text-sm border ${tab === 'punctuality' ? 'bg-slate-900 text-white border-slate-900' : 'bg-white text-slate-700 border-slate-200 hover:bg-slate-50'}`}
            onClick={() => setTab('punctuality')}
          >
            Punctuality
          </button>
          <button
            type="button"
            className={`px-3 py-1.5 rounded-full text-sm border ${tab === 'earnings' ? 'bg-slate-900 text-white border-slate-900' : 'bg-white text-slate-700 border-slate-200 hover:bg-slate-50'}`}
            onClick={() => setTab('earnings')}
          >
            Earnings
          </button>
          <button
            type="button"
            className={`px-3 py-1.5 rounded-full text-sm border ${tab === 'incidents' ? 'bg-slate-900 text-white border-slate-900' : 'bg-white text-slate-700 border-slate-200 hover:bg-slate-50'}`}
            onClick={() => setTab('incidents')}
          >
            Incidents
          </button>

          <div className="ml-auto flex items-center gap-2">
            <Badge tone="blue">{worker.rating.toFixed(1)} / 5</Badge>
            <Badge tone={worker.reliability >= 0.9 ? 'green' : worker.reliability >= 0.8 ? 'amber' : 'red'}>
              {toPercent(worker.reliability)} reliability
            </Badge>
          </div>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          {tab === 'overview' && (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-slate-700">Display name *</label>
                <input
                  type="text"
                  {...register('displayName', { required: 'Name is required' })}
                  className="mt-1 block w-full rounded-md border-slate-300 shadow-sm focus:border-slate-900 focus:ring-slate-900 sm:text-sm"
                />
                {errors.displayName && <p className="mt-1 text-sm text-red-600">{errors.displayName.message}</p>}
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700">Job profiles (comma separated)</label>
                <input
                  type="text"
                  {...register('jobProfiles')}
                  className="mt-1 block w-full rounded-md border-slate-300 shadow-sm focus:border-slate-900 focus:ring-slate-900 sm:text-sm"
                />
              </div>
              <div className="md:col-span-2">
                <label className="block text-sm font-medium text-slate-700">Skills (comma separated)</label>
                <input
                  type="text"
                  {...register('skills')}
                  className="mt-1 block w-full rounded-md border-slate-300 shadow-sm focus:border-slate-900 focus:ring-slate-900 sm:text-sm"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-slate-700">Rating (avg)</label>
                <input
                  type="number"
                  step="0.1"
                  min={0}
                  max={5}
                  {...register('rating', { valueAsNumber: true })}
                  className="mt-1 block w-full rounded-md border-slate-300 shadow-sm focus:border-slate-900 focus:ring-slate-900 sm:text-sm"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700"># of ratings</label>
                <input
                  type="number"
                  min={0}
                  {...register('ratingCount', { valueAsNumber: true })}
                  className="mt-1 block w-full rounded-md border-slate-300 shadow-sm focus:border-slate-900 focus:ring-slate-900 sm:text-sm"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-slate-700">Reliability (0–1)</label>
                <input
                  type="number"
                  step="0.01"
                  min={0}
                  max={1}
                  {...register('reliability', { valueAsNumber: true })}
                  className="mt-1 block w-full rounded-md border-slate-300 shadow-sm focus:border-slate-900 focus:ring-slate-900 sm:text-sm"
                />
                <p className="mt-1 text-xs text-slate-500">Shown as {toPercent(watch('reliability'))}</p>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700">Acceptance rate (0–1)</label>
                <input
                  type="number"
                  step="0.01"
                  min={0}
                  max={1}
                  {...register('acceptanceRate', { valueAsNumber: true })}
                  className="mt-1 block w-full rounded-md border-slate-300 shadow-sm focus:border-slate-900 focus:ring-slate-900 sm:text-sm"
                />
                <p className="mt-1 text-xs text-slate-500">Shown as {toPercent(watch('acceptanceRate'))}</p>
              </div>

              <div className="md:col-span-2">
                <label className="block text-sm font-medium text-slate-700">Notes</label>
                <textarea
                  rows={3}
                  {...register('notes')}
                  className="mt-1 block w-full rounded-md border-slate-300 shadow-sm focus:border-slate-900 focus:ring-slate-900 sm:text-sm"
                />
              </div>
            </div>
          )}

          {tab === 'punctuality' && (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-slate-700"># on-time starts</label>
                <input
                  type="number"
                  min={0}
                  {...register('onTimeStarts', { valueAsNumber: true })}
                  className="mt-1 block w-full rounded-md border-slate-300 shadow-sm focus:border-slate-900 focus:ring-slate-900 sm:text-sm"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700"># late starts</label>
                <input
                  type="number"
                  min={0}
                  {...register('lateStarts', { valueAsNumber: true })}
                  className="mt-1 block w-full rounded-md border-slate-300 shadow-sm focus:border-slate-900 focus:ring-slate-900 sm:text-sm"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-slate-700"># jobs completed</label>
                <input
                  type="number"
                  min={0}
                  {...register('jobsCompleted', { valueAsNumber: true })}
                  className="mt-1 block w-full rounded-md border-slate-300 shadow-sm focus:border-slate-900 focus:ring-slate-900 sm:text-sm"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700"># jobs not completed</label>
                <input
                  type="number"
                  min={0}
                  {...register('jobsNotCompleted', { valueAsNumber: true })}
                  className="mt-1 block w-full rounded-md border-slate-300 shadow-sm focus:border-slate-900 focus:ring-slate-900 sm:text-sm"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700"># no-shows</label>
                <input
                  type="number"
                  min={0}
                  {...register('noShows', { valueAsNumber: true })}
                  className="mt-1 block w-full rounded-md border-slate-300 shadow-sm focus:border-slate-900 focus:ring-slate-900 sm:text-sm"
                />
              </div>
            </div>
          )}

          {tab === 'earnings' && (
            <div className="space-y-4">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700">Currency</label>
                  <input
                    type="text"
                    {...register('currency')}
                    className="mt-1 block w-full rounded-md border-slate-300 shadow-sm focus:border-slate-900 focus:ring-slate-900 sm:text-sm"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700">Avg hourly rate (cents)</label>
                  <input
                    type="number"
                    min={0}
                    {...register('avgHourlyRateCents', { valueAsNumber: true })}
                    className="mt-1 block w-full rounded-md border-slate-300 shadow-sm focus:border-slate-900 focus:ring-slate-900 sm:text-sm"
                  />
                  <p className="mt-1 text-xs text-slate-500">
                    Shown as {money(watch('avgHourlyRateCents'), currency || 'CHF')} / hour
                  </p>
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700">Total last month (cents)</label>
                  <input
                    type="number"
                    min={0}
                    {...register('lastMonthCents', { valueAsNumber: true })}
                    className="mt-1 block w-full rounded-md border-slate-300 shadow-sm focus:border-slate-900 focus:ring-slate-900 sm:text-sm"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700">Total 3 months (cents)</label>
                  <input
                    type="number"
                    min={0}
                    {...register('last3MonthsCents', { valueAsNumber: true })}
                    className="mt-1 block w-full rounded-md border-slate-300 shadow-sm focus:border-slate-900 focus:ring-slate-900 sm:text-sm"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700">Total 6 months (cents)</label>
                  <input
                    type="number"
                    min={0}
                    {...register('last6MonthsCents', { valueAsNumber: true })}
                    className="mt-1 block w-full rounded-md border-slate-300 shadow-sm focus:border-slate-900 focus:ring-slate-900 sm:text-sm"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700">Total 12 months (cents)</label>
                  <input
                    type="number"
                    min={0}
                    {...register('last12MonthsCents', { valueAsNumber: true })}
                    className="mt-1 block w-full rounded-md border-slate-300 shadow-sm focus:border-slate-900 focus:ring-slate-900 sm:text-sm"
                  />
                </div>
              </div>

              <div className="rounded-xl border border-slate-200 bg-slate-50 p-3 text-sm text-slate-700">
                <div className="font-medium mb-1">Preview</div>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-2">
                  <div>Last month: <span className="font-semibold">{money(watch('lastMonthCents'), currency || 'CHF')}</span></div>
                  <div>3 months: <span className="font-semibold">{money(watch('last3MonthsCents'), currency || 'CHF')}</span></div>
                  <div>6 months: <span className="font-semibold">{money(watch('last6MonthsCents'), currency || 'CHF')}</span></div>
                  <div>12 months: <span className="font-semibold">{money(watch('last12MonthsCents'), currency || 'CHF')}</span></div>
                </div>
              </div>
            </div>
          )}

          {tab === 'incidents' && (
            <div className="space-y-4">
              <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
                <div>
                  <label className="block text-sm font-medium text-slate-700">Type</label>
                  <select
                    value={newIncidentType}
                    onChange={(e) => setNewIncidentType(e.target.value as WorkerIncidentType)}
                    className="mt-1 block w-full rounded-md border-slate-300 shadow-sm focus:border-slate-900 focus:ring-slate-900 sm:text-sm"
                  >
                    <option value="late_start">Late start</option>
                    <option value="no_show">No show</option>
                    <option value="early_leave">Early leave</option>
                    <option value="policy_violation">Policy violation</option>
                    <option value="quality_issue">Quality issue</option>
                    <option value="other">Other</option>
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700">Severity</label>
                  <select
                    value={newIncidentSeverity}
                    onChange={(e) => setNewIncidentSeverity(e.target.value as WorkerIncidentSeverity)}
                    className="mt-1 block w-full rounded-md border-slate-300 shadow-sm focus:border-slate-900 focus:ring-slate-900 sm:text-sm"
                  >
                    <option value="low">Low</option>
                    <option value="medium">Medium</option>
                    <option value="high">High</option>
                    <option value="critical">Critical</option>
                  </select>
                </div>
                <div className="md:col-span-3">
                  <label className="block text-sm font-medium text-slate-700">Summary</label>
                  <div className="mt-1 flex gap-2">
                    <input
                      type="text"
                      value={newIncidentSummary}
                      onChange={(e) => setNewIncidentSummary(e.target.value)}
                      placeholder="e.g. Arrived 15 minutes late (no notice)"
                      className="flex-1 block w-full rounded-md border-slate-300 shadow-sm focus:border-slate-900 focus:ring-slate-900 sm:text-sm"
                    />
                    <Button type="button" onClick={addIncident} disabled={updateMutation.isPending || !newIncidentSummary.trim()}>
                      Add incident
                    </Button>
                  </div>
                </div>
              </div>

              <div className="rounded-xl border border-slate-200 overflow-hidden">
                <div className="bg-slate-50 px-3 py-2 text-sm font-semibold text-slate-700">
                  Last incidents (max 20)
                </div>
                <div className="divide-y divide-slate-100">
                  {(worker.lastIncidents ?? []).slice(0, 20).map((inc) => (
                    <div key={inc.id} className="px-3 py-2 text-sm flex items-start gap-2">
                      <Badge
                        tone={inc.severity === 'critical' ? 'red' : inc.severity === 'high' ? 'amber' : 'blue'}
                      >
                        {inc.type.replace('_', ' ')}
                      </Badge>
                      <div className="flex-1">
                        <div className="text-slate-800">{inc.summary}</div>
                        <div className="text-xs text-slate-500">
                          {new Date(inc.occurredAt).toLocaleString()}
                        </div>
                      </div>
                    </div>
                  ))}
                  {(worker.lastIncidents ?? []).length === 0 && (
                    <div className="px-3 py-6 text-sm text-slate-500 text-center">No incidents recorded.</div>
                  )}
                </div>
              </div>
            </div>
          )}

          <div className="flex justify-end gap-2 pt-2">
            <Button type="button" variant="secondary" onClick={onClose}>
              Cancel
            </Button>
            <Button type="submit" disabled={updateMutation.isPending}>
              {updateMutation.isPending ? 'Saving…' : 'Save changes'}
            </Button>
          </div>
        </form>
      </div>
    </Modal>
  );
}

