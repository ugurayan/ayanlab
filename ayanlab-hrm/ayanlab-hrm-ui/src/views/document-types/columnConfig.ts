import { createTableColumnConfig, type TableColumnConfig } from '../../lib/tableColumnConfig';

export const DOCUMENT_TYPE_COLUMN_IDS = [
  'id',
  'name',
  'description',
  'createdBy',
  'createdAt',
  'updatedBy',
  'updatedAt',
] as const;

export type DocumentTypeColumnId = (typeof DOCUMENT_TYPE_COLUMN_IDS)[number];

export const DOCUMENT_TYPE_COLUMN_LABELS: Record<DocumentTypeColumnId, string> = {
  id: 'ID',
  name: 'Name',
  description: 'Description',
  createdBy: 'Created By',
  createdAt: 'Created At',
  updatedBy: 'Updated By',
  updatedAt: 'Updated At',
};

const config = createTableColumnConfig(
  'document-types-column-config',
  DOCUMENT_TYPE_COLUMN_IDS,
  DOCUMENT_TYPE_COLUMN_LABELS
);

export type DocumentTypeColumnConfig = TableColumnConfig<DocumentTypeColumnId>;
export const loadDocumentTypeColumnConfig = config.load;
export const saveDocumentTypeColumnConfig = config.save;
export const getVisibleColumnsInOrder = config.getVisibleColumnsInOrder;
