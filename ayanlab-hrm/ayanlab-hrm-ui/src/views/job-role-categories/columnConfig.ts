import { createTableColumnConfig, type TableColumnConfig } from '../../lib/tableColumnConfig';

export const JOB_ROLE_CATEGORY_COLUMN_IDS = [
  'id',
  'name',
  'description',
  'createdBy',
  'createdAt',
  'updatedBy',
  'updatedAt',
] as const;

export type JobRoleCategoryColumnId = (typeof JOB_ROLE_CATEGORY_COLUMN_IDS)[number];

export const JOB_ROLE_CATEGORY_COLUMN_LABELS: Record<JobRoleCategoryColumnId, string> = {
  id: 'ID',
  name: 'Name',
  description: 'Description',
  createdBy: 'Created By',
  createdAt: 'Created At',
  updatedBy: 'Updated By',
  updatedAt: 'Updated At',
};

const config = createTableColumnConfig(
  'job-role-categories-column-config',
  JOB_ROLE_CATEGORY_COLUMN_IDS,
  JOB_ROLE_CATEGORY_COLUMN_LABELS
);

export type JobRoleCategoryColumnConfig = TableColumnConfig<JobRoleCategoryColumnId>;
export const loadJobRoleCategoryColumnConfig = config.load;
export const saveJobRoleCategoryColumnConfig = config.save;
export const getVisibleColumnsInOrder = config.getVisibleColumnsInOrder;
