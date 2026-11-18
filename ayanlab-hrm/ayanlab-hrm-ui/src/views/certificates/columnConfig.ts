import { createTableColumnConfig, type TableColumnConfig } from '../../lib/tableColumnConfig';

export const CERTIFICATE_COLUMN_IDS = [
  'id',
  'name',
  'description',
  'createdBy',
  'createdAt',
  'updatedBy',
  'updatedAt',
] as const;

export type CertificateColumnId = (typeof CERTIFICATE_COLUMN_IDS)[number];

export const CERTIFICATE_COLUMN_LABELS: Record<CertificateColumnId, string> = {
  id: 'ID',
  name: 'Name',
  description: 'Description',
  createdBy: 'Created By',
  createdAt: 'Created At',
  updatedBy: 'Updated By',
  updatedAt: 'Updated At',
};

const config = createTableColumnConfig(
  'certificates-column-config',
  CERTIFICATE_COLUMN_IDS,
  CERTIFICATE_COLUMN_LABELS
);

export type CertificateColumnConfig = TableColumnConfig<CertificateColumnId>;
export const loadCertificateColumnConfig = config.load;
export const saveCertificateColumnConfig = config.save;
export const getVisibleColumnsInOrder = config.getVisibleColumnsInOrder;
