import { createTableColumnConfig, type TableColumnConfig } from '../../lib/tableColumnConfig';

export const SKILL_COLUMN_IDS = [
  'id',
  'name',
  'description',
  'createdBy',
  'createdAt',
  'updatedBy',
  'updatedAt',
] as const;

export type SkillColumnId = (typeof SKILL_COLUMN_IDS)[number];

export const SKILL_COLUMN_LABELS: Record<SkillColumnId, string> = {
  id: 'ID',
  name: 'Name',
  description: 'Description',
  createdBy: 'Created By',
  createdAt: 'Created At',
  updatedBy: 'Updated By',
  updatedAt: 'Updated At',
};

const config = createTableColumnConfig(
  'skills-column-config',
  SKILL_COLUMN_IDS,
  SKILL_COLUMN_LABELS
);

export type SkillColumnConfig = TableColumnConfig<SkillColumnId>;
export const loadSkillColumnConfig = config.load;
export const saveSkillColumnConfig = config.save;
export const getVisibleColumnsInOrder = config.getVisibleColumnsInOrder;
