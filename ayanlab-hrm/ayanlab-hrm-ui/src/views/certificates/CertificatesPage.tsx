import React, { useState, useCallback } from 'react';
import { useCertificates, useDeleteCertificate } from '../../data/hooks/useCertificates';
import { Button } from '../../ui/components/Button';
import { useAuth } from '../../state/auth';
import { CertificateDetail } from './CertificateDetail';
import { LoadingOverlay } from '../../ui/components/LoadingSpinner';
import type { Certificate } from '../../api/services/certificatesAdmin';
import { Table, Th, Td } from '../../ui/components/Table';
import { formatDateTime } from '../../lib/format';
import { ColumnsPanel } from '../../ui/components/ColumnsPanel';
import {
  loadCertificateColumnConfig,
  saveCertificateColumnConfig,
  getVisibleColumnsInOrder,
  CERTIFICATE_COLUMN_IDS,
  CERTIFICATE_COLUMN_LABELS,
  type CertificateColumnConfig,
  type CertificateColumnId,
} from './columnConfig';
import { Columns2 } from 'lucide-react';

export function CertificatesPage() {
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [columnConfig, setColumnConfig] = useState<CertificateColumnConfig>(() => loadCertificateColumnConfig());
  const [columnsPanelOpen, setColumnsPanelOpen] = useState(false);
  const { data, isLoading, error } = useCertificates(page, pageSize);
  const deleteMutation = useDeleteCertificate();
  const { has } = useAuth();
  const [selectedCertificate, setSelectedCertificate] = useState<Certificate | null>(null);
  const [isCreateMode, setIsCreateMode] = useState(false);
  const [openMenuId, setOpenMenuId] = useState<string | null>(null);

  const visibleColumns = getVisibleColumnsInOrder(columnConfig);
  const handleColumnConfigSave = useCallback((config: CertificateColumnConfig) => {
    saveCertificateColumnConfig(config);
    setColumnConfig(config);
  }, []);

  const certificates = data?.items ?? [];
  const totalPages = data?.totalPages ?? 0;
  const totalElements = data?.totalElements ?? 0;

  const handleUpdate = (certificate: Certificate) => {
    setSelectedCertificate(certificate);
    setIsCreateMode(false);
    setOpenMenuId(null);
  };

  const handleCreateNew = () => {
    setSelectedCertificate(null);
    setIsCreateMode(true);
    setOpenMenuId(null);
  };

  const handleDelete = async (certificate: Certificate) => {
    const confirmed = window.confirm(`Delete certificate "${certificate.name}"?`);
    if (!confirmed) return;
    try {
      await deleteMutation.mutateAsync(certificate.id);
      setOpenMenuId(null);
      alert('Certificate deleted successfully.');
    } catch (err: any) {
      console.error('Delete certificate failed', err);
      alert(err?.message || 'Failed to delete certificate.');
    }
  };

  const handleClose = () => {
    setSelectedCertificate(null);
    setIsCreateMode(false);
  };

  const handlePageSizeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const newSize = Number(e.target.value);
    setPageSize(newSize);
    setPage(0);
  };

  function renderCell(certificate: Certificate, columnId: CertificateColumnId) {
    switch (columnId) {
      case 'id':
        return <Td className="text-xs text-slate-500">{certificate.id}</Td>;
      case 'name':
        return <Td className="font-semibold">{certificate.name}</Td>;
      case 'description':
        return <Td className="text-slate-600">{certificate.description || 'No description'}</Td>;
      case 'createdBy':
        return <Td className="text-xs text-slate-500">{certificate.createdBy}</Td>;
      case 'createdAt':
        return (
          <Td className="text-xs text-slate-500">
            {certificate.createdAt ? formatDateTime(new Date(certificate.createdAt).toISOString()) : '-'}
          </Td>
        );
      case 'updatedBy':
        return <Td className="text-xs text-slate-500">{certificate.updatedBy || '-'}</Td>;
      case 'updatedAt':
        return (
          <Td className="text-xs text-slate-500">
            {certificate.updatedAt ? formatDateTime(new Date(certificate.updatedAt).toISOString()) : '-'}
          </Td>
        );
      default:
        return null;
    }
  }

  const thClass = (id: CertificateColumnId) => (id === 'id' ? 'w-[220px]' : undefined);

  if (error) return <div className="p-6">Error loading certificates</div>;

  return (
    <div className="h-full flex flex-col relative">
      {isLoading && <LoadingOverlay text="Loading certificates..." />}
      
      <div className="flex-shrink-0 p-6 pb-4">
        <div className="flex justify-between items-center mb-4">
          <h1 className="text-2xl font-bold">Certificates</h1>
          <div className="flex items-center gap-2">
            <Button variant="secondary" onClick={() => setColumnsPanelOpen(true)} className="inline-flex items-center gap-2">
              <Columns2 size={18} />
              Columns
            </Button>
            {has('shift:read') && (
              <Button onClick={handleCreateNew}>
                + Add New Certificate
              </Button>
            )}
          </div>
        </div>

        <div className="flex items-center justify-between text-sm text-slate-600">
          <div>
            Total certificates: <span className="font-semibold">{totalElements}</span>
          </div>
          <div className="flex items-center gap-2">
            <span>Rows per page:</span>
            <select
              value={pageSize}
              onChange={handlePageSizeChange}
              className="border border-slate-200 rounded-md px-2 py-1 text-sm"
            >
              <option value={10}>10</option>
              <option value={25}>25</option>
              <option value={50}>50</option>
              <option value={100}>100</option>
            </select>
          </div>
        </div>
      </div>

      <div className="flex-1 min-h-0 px-6 pb-4">
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden h-full flex flex-col">
          <div className="overflow-y-auto flex-1">
            <Table>
              <thead className="bg-slate-50 sticky top-0 z-10">
                <tr>
                  {visibleColumns.map((id) => (
                    <Th key={id} className={thClass(id)}>
                      {CERTIFICATE_COLUMN_LABELS[id]}
                    </Th>
                  ))}
                  <Th className="text-right w-[80px]">Actions</Th>
                </tr>
              </thead>
              <tbody>
                {certificates.map((certificate) => (
                  <tr key={certificate.id} className="hover:bg-slate-50">
                    {visibleColumns.map((id) => (
                      <React.Fragment key={id}>{renderCell(certificate, id)}</React.Fragment>
                    ))}
                    <Td className="text-right relative">
                      <button
                        type="button"
                        className="inline-flex h-8 w-8 items-center justify-center rounded-full border border-slate-200 text-slate-600 hover:bg-slate-100"
                        onClick={() =>
                          setOpenMenuId((current) => (current === certificate.id ? null : certificate.id))
                        }
                      >
                        …
                      </button>
                      {openMenuId === certificate.id && (
                        <div className="absolute right-0 mt-2 w-40 rounded-xl border border-slate-200 bg-white shadow-md z-10">
                          <button
                            type="button"
                            className="w-full px-3 py-2 text-left text-sm hover:bg-slate-50"
                            onClick={() => handleUpdate(certificate)}
                          >
                            Update
                          </button>
                          <button
                            type="button"
                            className="w-full px-3 py-2 text-left text-sm hover:bg-slate-50"
                            onClick={handleCreateNew}
                          >
                            Create new
                          </button>
                          <button
                            type="button"
                            className="w-full px-3 py-2 text-left text-sm text-red-600 hover:bg-slate-50"
                            onClick={() => handleDelete(certificate)}
                            disabled={deleteMutation.isPending}
                          >
                            {deleteMutation.isPending ? 'Deleting...' : 'Delete'}
                          </button>
                        </div>
                      )}
                    </Td>
                  </tr>
                ))}

                {certificates.length === 0 && !isLoading && (
                  <tr>
                    <Td colSpan={visibleColumns.length + 1} className="text-center text-slate-500 py-6">
                      No certificates found.
                    </Td>
                  </tr>
                )}
              </tbody>
            </Table>
          </div>
        </div>
      </div>

      <div className="flex-shrink-0 px-6 pb-6 pt-4 flex items-center justify-between text-sm text-slate-600 border-t border-slate-100">
        <div>
          Page <span className="font-semibold">{totalPages === 0 ? 0 : page + 1}</span> of{" "}
          <span className="font-semibold">{totalPages}</span>
        </div>
        <div className="flex gap-2">
          <Button
            variant="secondary"
            disabled={page === 0}
            onClick={() => setPage((p) => Math.max(0, p - 1))}
          >
            Previous
          </Button>
          <Button
            variant="secondary"
            disabled={page + 1 >= totalPages}
            onClick={() => setPage((p) => p + 1)}
          >
            Next
          </Button>
        </div>
      </div>

      {(selectedCertificate || isCreateMode) && (
        <CertificateDetail 
          certificate={selectedCertificate ?? undefined}
          isOpen={!!selectedCertificate || isCreateMode}
          onClose={handleClose}
          isCreateMode={isCreateMode}
        />
      )}

      <ColumnsPanel
        isOpen={columnsPanelOpen}
        onClose={() => setColumnsPanelOpen(false)}
        columnIds={CERTIFICATE_COLUMN_IDS}
        labels={CERTIFICATE_COLUMN_LABELS}
        currentConfig={columnConfig}
        onSave={handleColumnConfigSave}
      />
    </div>
  );
}
