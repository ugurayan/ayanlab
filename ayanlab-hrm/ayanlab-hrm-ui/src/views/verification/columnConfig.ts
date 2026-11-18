import { createTableColumnConfig, type TableColumnConfig } from '../../lib/tableColumnConfig';

export const VERIFICATION_COLUMN_IDS = [
  'worker',
  'type',
  'category',
  'status',
  'priority',
  'submittedAt',
  'assignedTo',
] as const;

export type VerificationColumnId = (typeof VERIFICATION_COLUMN_IDS)[number];

export const VERIFICATION_COLUMN_LABELS: Record<VerificationColumnId, string> = {
  worker: 'Worker',
  type: 'Type',
  category: 'Category',
  status: 'Status',
  priority: 'Priority',
  submittedAt: 'Submitted',
  assignedTo: 'Assigned to',
};

const config = createTableColumnConfig(
  'verification-column-config',
  VERIFICATION_COLUMN_IDS,
  VERIFICATION_COLUMN_LABELS
);

export type VerificationColumnConfig = TableColumnConfig<VerificationColumnId>;
export const loadVerificationColumnConfig = config.load;
export const saveVerificationColumnConfig = config.save;
export const getVisibleColumnsInOrder = config.getVisibleColumnsInOrder;

