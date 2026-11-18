import { createTableColumnConfig, type TableColumnConfig } from '../../lib/tableColumnConfig';

export const JOB_ROLE_COLUMN_IDS = [
  'id',
  'name',
  'description',
  'category',
  'createdBy',
  'createdAt',
  'updatedBy',
  'updatedAt',
] as const;

export type JobRoleColumnId = (typeof JOB_ROLE_COLUMN_IDS)[number];

export const JOB_ROLE_COLUMN_LABELS: Record<JobRoleColumnId, string> = {
  id: 'ID',
  name: 'Name',
  description: 'Description',
  category: 'Category',
  createdBy: 'Created By',
  createdAt: 'Created At',
  updatedBy: 'Updated By',
  updatedAt: 'Updated At',
};

const config = createTableColumnConfig(
  'job-roles-column-config',
  JOB_ROLE_COLUMN_IDS,
  JOB_ROLE_COLUMN_LABELS
);

export type JobRoleColumnConfig = TableColumnConfig<JobRoleColumnId>;
export const loadJobRoleColumnConfig = config.load;
export const saveJobRoleColumnConfig = config.save;
export const getVisibleColumnsInOrder = config.getVisibleColumnsInOrder;
