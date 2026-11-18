import { createTableColumnConfig, type TableColumnConfig } from '../../lib/tableColumnConfig';

export const WORKER_COLUMN_IDS = [
  'name',
  'jobProfiles',
  'skills',
  'rating',
  'ratingCount',
  'reliability',
  'acceptanceRate',
  'onTimeStarts',
  'lateStarts',
  'jobsCompleted',
  'jobsNotCompleted',
  'noShows',
  'criticalIncidents',
  'reliabilityIncidents',
  'avgHourlyRate',
  'earningsLastMonth',
  'earningsLast3Months',
  'earningsLast6Months',
  'earningsLast12Months',
  'notes',
] as const;

export type WorkerColumnId = (typeof WORKER_COLUMN_IDS)[number];

export const WORKER_COLUMN_LABELS: Record<WorkerColumnId, string> = {
  name: 'Worker',
  jobProfiles: 'Job profiles',
  skills: 'Skills',
  rating: 'Rating (avg)',
  ratingCount: '# ratings',
  reliability: 'Reliability',
  acceptanceRate: 'Acceptance rate',
  onTimeStarts: '# on-time starts',
  lateStarts: '# late starts',
  jobsCompleted: '# jobs completed',
  jobsNotCompleted: '# jobs not completed',
  noShows: '# no-shows',
  criticalIncidents: '# critical incidents',
  reliabilityIncidents: '# reliability incidents',
  avgHourlyRate: 'Avg hourly rate',
  earningsLastMonth: 'Total (last month)',
  earningsLast3Months: 'Total (3 months)',
  earningsLast6Months: 'Total (6 months)',
  earningsLast12Months: 'Total (12 months)',
  notes: 'Notes',
};

const config = createTableColumnConfig(
  'workers-column-config',
  WORKER_COLUMN_IDS,
  WORKER_COLUMN_LABELS
);

export type WorkerColumnConfig = TableColumnConfig<WorkerColumnId>;
export const loadWorkerColumnConfig = config.load;
export const saveWorkerColumnConfig = config.save;
export const getVisibleColumnsInOrder = config.getVisibleColumnsInOrder;

