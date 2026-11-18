import React, { useState, useCallback } from 'react';
import { useJobRoleCategories, useDeleteJobRoleCategory } from '../../data/hooks/useJobRoleCategories';
import { Button } from '../../ui/components/Button';
import { useAuth } from '../../state/auth';
import { JobRoleCategoryDetail } from './JobRoleCategoryDetail';
import { LoadingOverlay } from '../../ui/components/LoadingSpinner';
import type { JobRoleCategory } from '../../types/jobRoles';
import { Table, Th, Td } from '../../ui/components/Table';
import { formatDateTime } from '../../lib/format';
import { ColumnsPanel } from '../../ui/components/ColumnsPanel';
import {
  loadJobRoleCategoryColumnConfig,
  saveJobRoleCategoryColumnConfig,
  getVisibleColumnsInOrder,
  JOB_ROLE_CATEGORY_COLUMN_IDS,
  JOB_ROLE_CATEGORY_COLUMN_LABELS,
  type JobRoleCategoryColumnConfig,
  type JobRoleCategoryColumnId,
} from './columnConfig';
import { Columns2 } from 'lucide-react';
import { P } from "../../security/permissions";

export function JobRoleCategoriesList() {
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [columnConfig, setColumnConfig] = useState<JobRoleCategoryColumnConfig>(() =>
    loadJobRoleCategoryColumnConfig()
  );
  const [columnsPanelOpen, setColumnsPanelOpen] = useState(false);
  const { data, isLoading, error } = useJobRoleCategories(page, pageSize);
  const deleteMutation = useDeleteJobRoleCategory();
  const { has } = useAuth();
  const [selectedCategory, setSelectedCategory] = useState<JobRoleCategory | null>(null);
  const [isCreateMode, setIsCreateMode] = useState(false);
  const [openMenuId, setOpenMenuId] = useState<string | null>(null);

  const visibleColumns = getVisibleColumnsInOrder(columnConfig);
  const handleColumnConfigSave = useCallback((config: JobRoleCategoryColumnConfig) => {
    saveJobRoleCategoryColumnConfig(config);
    setColumnConfig(config);
  }, []);

  const categories = data?.items ?? [];
  const totalPages = data?.totalPages ?? 0;
  const totalElements = data?.totalElements ?? 0;

  const handleUpdate = (category: JobRoleCategory) => {
    setSelectedCategory(category);
    setIsCreateMode(false);
    setOpenMenuId(null);
  };

  const handleCreateNew = () => {
    setSelectedCategory(null);
    setIsCreateMode(true);
    setOpenMenuId(null);
  };

  const handleDelete = async (category: JobRoleCategory) => {
    const confirmed = window.confirm(`Delete category "${category.name}"?`);
    if (!confirmed) return;
    try {
      await deleteMutation.mutateAsync(category.id);
      setOpenMenuId(null);
      alert('Category deleted successfully.');
    } catch (err: any) {
      console.error('Delete category failed', err);
      alert(err?.message || 'Failed to delete category.');
    }
  };

  const handleClose = () => {
    setSelectedCategory(null);
    setIsCreateMode(false);
  };

  const handlePageSizeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const newSize = Number(e.target.value);
    setPageSize(newSize);
    setPage(0);
  };

  function renderCell(category: JobRoleCategory, columnId: JobRoleCategoryColumnId) {
    switch (columnId) {
      case 'id':
        return <Td className="text-xs text-slate-500">{category.id}</Td>;
      case 'name':
        return <Td className="font-semibold">{category.name}</Td>;
      case 'description':
        return <Td className="text-slate-600">{category.description || 'No description'}</Td>;
      case 'createdBy':
        return <Td className="text-xs text-slate-500">{category.createdBy}</Td>;
      case 'createdAt':
        return (
          <Td className="text-xs text-slate-500">
            {category.createdAt ? formatDateTime(new Date(category.createdAt).toISOString()) : '-'}
          </Td>
        );
      case 'updatedBy':
        return <Td className="text-xs text-slate-500">{category.updatedBy || '-'}</Td>;
      case 'updatedAt':
        return (
          <Td className="text-xs text-slate-500">
            {category.updatedAt ? formatDateTime(new Date(category.updatedAt).toISOString()) : '-'}
          </Td>
        );
      default:
        return null;
    }
  }

  const thClass = (id: JobRoleCategoryColumnId) => (id === 'id' ? 'w-[220px]' : undefined);

  if (error) return <div className="p-6">Error loading categories</div>;

  return (
    <div className="p-6 relative space-y-4">
      {isLoading && <LoadingOverlay text="Loading categories..." />}

      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold">Job Role Categories</h1>
        <div className="flex items-center gap-2">
          <Button variant="secondary" onClick={() => setColumnsPanelOpen(true)} className="inline-flex items-center gap-2">
            <Columns2 size={18} />
            Columns
          </Button>
          {has(P.jobRolesCreate) && (
            <Button onClick={handleCreateNew}>
              + Add New Category
            </Button>
          )}
        </div>
      </div>

      <div className="flex items-center justify-between text-sm text-slate-600">
        <div>
          Total categories: <span className="font-semibold">{totalElements}</span>
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

      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <Table>
          <thead className="bg-slate-50">
            <tr>
              {visibleColumns.map((id) => (
                <Th key={id} className={thClass(id)}>
                  {JOB_ROLE_CATEGORY_COLUMN_LABELS[id]}
                </Th>
              ))}
              <Th className="text-right w-[80px]">Actions</Th>
            </tr>
          </thead>
          <tbody>
            {categories.map((category) => (
              <tr key={category.id} className="hover:bg-slate-50">
                {visibleColumns.map((id) => (
                  <React.Fragment key={id}>{renderCell(category, id)}</React.Fragment>
                ))}
                <Td className="text-right relative">
                  <button
                    type="button"
                    className="inline-flex h-8 w-8 items-center justify-center rounded-full border border-slate-200 text-slate-600 hover:bg-slate-100"
                    onClick={() =>
                      setOpenMenuId((current) => (current === category.id ? null : category.id))
                    }
                  >
                    …
                  </button>
                  {openMenuId === category.id && (
                    <div className="absolute right-0 mt-2 w-40 rounded-xl border border-slate-200 bg-white shadow-md z-10">
                      <button
                        type="button"
                        className="w-full px-3 py-2 text-left text-sm hover:bg-slate-50"
                        onClick={() => handleUpdate(category)}
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
                        onClick={() => handleDelete(category)}
                        disabled={deleteMutation.isPending}
                      >
                        {deleteMutation.isPending ? 'Deleting...' : 'Delete'}
                      </button>
                    </div>
                  )}
                </Td>
              </tr>
            ))}

            {categories.length === 0 && !isLoading && (
              <tr>
                <Td colSpan={visibleColumns.length + 1} className="text-center text-slate-500 py-6">
                  No job role categories found.
                </Td>
              </tr>
            )}
          </tbody>
        </Table>
      </div>

      <div className="flex items-center justify-between text-sm text-slate-600">
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

      {(selectedCategory || isCreateMode) && (
        <JobRoleCategoryDetail 
          category={selectedCategory ?? undefined}
          isOpen={!!selectedCategory || isCreateMode}
          onClose={handleClose}
          isCreateMode={isCreateMode}
        />
      )}

      <ColumnsPanel
        isOpen={columnsPanelOpen}
        onClose={() => setColumnsPanelOpen(false)}
        columnIds={JOB_ROLE_CATEGORY_COLUMN_IDS}
        labels={JOB_ROLE_CATEGORY_COLUMN_LABELS}
        currentConfig={columnConfig}
        onSave={handleColumnConfigSave}
      />
    </div>
  );
}
