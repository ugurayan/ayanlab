import React, { useState, useCallback } from 'react';
import { useDocumentTypes, useDeleteDocumentType } from '../../data/hooks/useDocumentTypes';
import { Button } from '../../ui/components/Button';
import { useAuth } from '../../state/auth';
import { DocumentTypeDetail } from './DocumentTypeDetail';
import { LoadingOverlay } from '../../ui/components/LoadingSpinner';
import type { DocumentType } from '../../api/services/documentTypesAdmin';
import { Table, Th, Td } from '../../ui/components/Table';
import { formatDateTime } from '../../lib/format';
import { ColumnsPanel } from '../../ui/components/ColumnsPanel';
import {
  loadDocumentTypeColumnConfig,
  saveDocumentTypeColumnConfig,
  getVisibleColumnsInOrder,
  DOCUMENT_TYPE_COLUMN_IDS,
  DOCUMENT_TYPE_COLUMN_LABELS,
  type DocumentTypeColumnConfig,
  type DocumentTypeColumnId,
} from './columnConfig';
import { Columns2 } from 'lucide-react';
import { P } from "../../security/permissions";

export function DocumentTypesPage() {
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [columnConfig, setColumnConfig] = useState<DocumentTypeColumnConfig>(() => loadDocumentTypeColumnConfig());
  const [columnsPanelOpen, setColumnsPanelOpen] = useState(false);
  const { data, isLoading, error } = useDocumentTypes(page, pageSize);
  const deleteMutation = useDeleteDocumentType();
  const { has } = useAuth();
  const [selectedItem, setSelectedItem] = useState<DocumentType | null>(null);
  const [isCreateMode, setIsCreateMode] = useState(false);
  const [openMenuId, setOpenMenuId] = useState<string | null>(null);

  const visibleColumns = getVisibleColumnsInOrder(columnConfig);
  const handleColumnConfigSave = useCallback((config: DocumentTypeColumnConfig) => {
    saveDocumentTypeColumnConfig(config);
    setColumnConfig(config);
  }, []);

  const items = data?.items ?? [];
  const totalPages = data?.totalPages ?? 0;
  const totalElements = data?.totalElements ?? 0;

  const handleUpdate = (item: DocumentType) => {
    setSelectedItem(item);
    setIsCreateMode(false);
    setOpenMenuId(null);
  };

  const handleCreateNew = () => {
    setSelectedItem(null);
    setIsCreateMode(true);
    setOpenMenuId(null);
  };

  const handleDelete = async (item: DocumentType) => {
    const confirmed = window.confirm(`Delete document type "${item.name}"?`);
    if (!confirmed) return;
    try {
      await deleteMutation.mutateAsync(item.id);
      setOpenMenuId(null);
      alert('Document type deleted successfully.');
    } catch (err: any) {
      console.error('Delete document type failed', err);
      alert(err?.message || 'Failed to delete document type.');
    }
  };

  const handleClose = () => {
    setSelectedItem(null);
    setIsCreateMode(false);
  };

  const handlePageSizeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const newSize = Number(e.target.value);
    setPageSize(newSize);
    setPage(0);
  };

  function renderCell(item: DocumentType, columnId: DocumentTypeColumnId) {
    switch (columnId) {
      case 'id':
        return <Td className="text-xs text-slate-500">{item.id}</Td>;
      case 'name':
        return <Td className="font-semibold">{item.name}</Td>;
      case 'description':
        return <Td className="text-slate-600">{item.description || 'No description'}</Td>;
      case 'createdBy':
        return <Td className="text-xs text-slate-500">{item.createdBy}</Td>;
      case 'createdAt':
        return (
          <Td className="text-xs text-slate-500">
            {item.createdAt
              ? formatDateTime(new Date(item.createdAt).toISOString())
              : '-'}
          </Td>
        );
      case 'updatedBy':
        return <Td className="text-xs text-slate-500">{item.updatedBy || '-'}</Td>;
      case 'updatedAt':
        return (
          <Td className="text-xs text-slate-500">
            {item.updatedAt
              ? formatDateTime(new Date(item.updatedAt).toISOString())
              : '-'}
          </Td>
        );
      default:
        return null;
    }
  }

  const thClass = (id: DocumentTypeColumnId) =>
    id === 'id' ? 'w-[220px]' : undefined;

  if (error) return <div className="p-6">Error loading document types</div>;

  return (
    <div className="h-full flex flex-col relative">
      {isLoading && <LoadingOverlay text="Loading document types..." />}

      <div className="flex-shrink-0 p-6 pb-4">
        <div className="flex justify-between items-center mb-4">
          <h1 className="text-2xl font-bold">Document Types</h1>
          <div className="flex items-center gap-2">
            <Button
              variant="secondary"
              onClick={() => setColumnsPanelOpen(true)}
              className="inline-flex items-center gap-2"
            >
              <Columns2 size={18} />
              Columns
            </Button>
            {has(P.documentTypesCreate) && (
              <Button onClick={handleCreateNew}>
                + Add New Document Type
              </Button>
            )}
          </div>
        </div>

        <div className="flex items-center justify-between text-sm text-slate-600">
          <div>
            Total document types: <span className="font-semibold">{totalElements}</span>
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
                      {DOCUMENT_TYPE_COLUMN_LABELS[id]}
                    </Th>
                  ))}
                  <Th className="text-right w-[80px]">Actions</Th>
                </tr>
              </thead>
              <tbody>
                {items.map((item) => (
                  <tr key={item.id} className="hover:bg-slate-50">
                    {visibleColumns.map((id) => (
                      <React.Fragment key={id}>{renderCell(item, id)}</React.Fragment>
                    ))}
                    <Td className="text-right relative">
                      <button
                        type="button"
                        className="inline-flex h-8 w-8 items-center justify-center rounded-full border border-slate-200 text-slate-600 hover:bg-slate-100"
                        onClick={() =>
                          setOpenMenuId((current) => (current === item.id ? null : item.id))
                        }
                      >
                        …
                      </button>
                      {openMenuId === item.id && (
                        <div className="absolute right-0 mt-2 w-40 rounded-xl border border-slate-200 bg-white shadow-md z-10">
                          <button
                            type="button"
                            className="w-full px-3 py-2 text-left text-sm hover:bg-slate-50"
                            onClick={() => handleUpdate(item)}
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
                            onClick={() => handleDelete(item)}
                            disabled={deleteMutation.isPending}
                          >
                            {deleteMutation.isPending ? 'Deleting...' : 'Delete'}
                          </button>
                        </div>
                      )}
                    </Td>
                  </tr>
                ))}

                {items.length === 0 && !isLoading && (
                  <tr>
                    <Td colSpan={visibleColumns.length + 1} className="text-center text-slate-500 py-6">
                      No document types found.
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

      {(selectedItem || isCreateMode) && (
        <DocumentTypeDetail
          item={selectedItem ?? undefined}
          isOpen={!!selectedItem || isCreateMode}
          onClose={handleClose}
          isCreateMode={isCreateMode}
        />
      )}

      <ColumnsPanel
        isOpen={columnsPanelOpen}
        onClose={() => setColumnsPanelOpen(false)}
        columnIds={DOCUMENT_TYPE_COLUMN_IDS}
        labels={DOCUMENT_TYPE_COLUMN_LABELS}
        currentConfig={columnConfig}
        onSave={handleColumnConfigSave}
      />
    </div>
  );
}


