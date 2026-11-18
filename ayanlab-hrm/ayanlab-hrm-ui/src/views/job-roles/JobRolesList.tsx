import React, { useState, useMemo, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useJobRoles, useDeleteJobRole } from '../../data/hooks/useJobRoles';
import { useJobRoleCategories } from '../../data/hooks/useJobRoleCategories';
import { Button } from '../../ui/components/Button';
import { useAuth } from '../../state/auth';
import { JobRoleDetail } from './JobRoleDetail';
import { LoadingOverlay } from '../../ui/components/LoadingSpinner';
import type { JobRole } from '../../types/jobRoles';
import { formatDateTime } from '../../lib/format';
import { Table, Th, Td } from '../../ui/components/Table';
import { ColumnsPanel } from '../../ui/components/ColumnsPanel';
import {
  loadJobRoleColumnConfig,
  saveJobRoleColumnConfig,
  getVisibleColumnsInOrder,
  JOB_ROLE_COLUMN_IDS,
  JOB_ROLE_COLUMN_LABELS,
  type JobRoleColumnConfig,
  type JobRoleColumnId,
} from './columnConfig';
import { Columns2 } from 'lucide-react';
import { P } from "../../security/permissions";

export function JobRolesList() {
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [columnConfig, setColumnConfig] = useState<JobRoleColumnConfig>(() => loadJobRoleColumnConfig());
  const [columnsPanelOpen, setColumnsPanelOpen] = useState(false);
  const { data, isLoading, error } = useJobRoles(page, pageSize);
  const { data: categoryPage } = useJobRoleCategories(0, 200);
  const deleteMutation = useDeleteJobRole();
  const { has } = useAuth();
  const [selectedRole, setSelectedRole] = useState<JobRole | null>(null);
  const [isCreateMode, setIsCreateMode] = useState(false);
  const [openMenuId, setOpenMenuId] = useState<string | null>(null);
  const navigate = useNavigate();

  const visibleColumns = getVisibleColumnsInOrder(columnConfig);
  const handleColumnConfigSave = useCallback((config: JobRoleColumnConfig) => {
    saveJobRoleColumnConfig(config);
    setColumnConfig(config);
  }, []);

  const jobRoles = data?.items ?? [];
  const totalPages = data?.totalPages ?? 0;
  const totalElements = data?.totalElements ?? 0;

  const categoryNameById = useMemo(() => {
    const map = new Map<string, string>();
    categoryPage?.items.forEach((c) => {
      map.set(c.id, c.name);
    });
    return map;
  }, [categoryPage]);

  const handleEdit = (role: JobRole) => {
    setSelectedRole(role);
    setIsCreateMode(false);
    setOpenMenuId(null);
  };

  const handleCreateNew = () => {
    setSelectedRole(null);
    setIsCreateMode(true);
    setOpenMenuId(null);
  };

  const handleClose = () => {
    setSelectedRole(null);
    setIsCreateMode(false);
  };

  const handlePageSizeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const newSize = Number(e.target.value);
    setPageSize(newSize);
    setPage(0);
  };

  const handleDelete = async (role: JobRole) => {
    const confirmed = window.confirm(`Delete job role "${role.name}"?`);
    if (!confirmed) return;
    try {
      await deleteMutation.mutateAsync(role.id);
      setOpenMenuId(null);
      alert('Job role deleted successfully.');
    } catch (err: any) {
      console.error('Error deleting job role:', err);
      alert(err?.message || 'Failed to delete job role.');
    }
  };

  function renderCell(role: JobRole, columnId: JobRoleColumnId) {
    switch (columnId) {
      case 'id':
        return <Td className="text-xs text-gray-500">{role.id}</Td>;
      case 'name':
        return <Td className="font-semibold">{role.name}</Td>;
      case 'description':
        return <Td className="text-slate-600">{role.description}</Td>;
      case 'category':
        return (
          <Td className="text-sm text-gray-500">
            {categoryNameById.get(role.categoryId) || 'Uncategorized'}
          </Td>
        );
      case 'createdBy':
        return <Td className="text-sm text-gray-500">{role.createdBy}</Td>;
      case 'createdAt':
        return (
          <Td className="text-xs text-gray-500">
            {role.createdAt
              ? formatDateTime(new Date(role.createdAt).toISOString())
              : '-'}
          </Td>
        );
      case 'updatedBy':
        return <Td className="text-sm text-gray-500">{role.updatedBy || '-'}</Td>;
      case 'updatedAt':
        return (
          <Td className="text-xs text-gray-500">
            {role.updatedAt
              ? formatDateTime(new Date(role.updatedAt).toISOString())
              : '-'}
          </Td>
        );
      default:
        return null;
    }
  }

  const thClass = (id: JobRoleColumnId) => (id === 'id' ? 'w-[220px]' : undefined);

  if (error) return <div className="p-6">Error loading job roles</div>;

  return (
    <div className="p-6 relative space-y-4">
      {isLoading && <LoadingOverlay text="Loading job roles..." />}
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold">Job Roles</h1>
        <div className="flex items-center gap-2">
          <Button variant="secondary" onClick={() => setColumnsPanelOpen(true)} className="inline-flex items-center gap-2">
            <Columns2 size={18} />
            Columns
          </Button>
          {has(P.jobRolesCreate) && (
            <Button onClick={handleCreateNew}>
              + Add New Job Role
            </Button>
          )}
        </div>
      </div>

      <div className="flex items-center justify-between text-sm text-slate-600 mb-3">
        <div>
          Total job roles: <span className="font-semibold">{totalElements}</span>
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
                  {JOB_ROLE_COLUMN_LABELS[id]}
                </Th>
              ))}
              <Th className="text-right w-[80px]">Actions</Th>
            </tr>
          </thead>
          <tbody>
            {jobRoles?.map((role) => (
              <tr key={role.id} className="hover:bg-slate-50">
                {visibleColumns.map((id) => (
                  <React.Fragment key={id}>{renderCell(role, id)}</React.Fragment>
                ))}
                <Td className="text-right relative">
                  <button
                    type="button"
                    className="inline-flex h-8 w-8 items-center justify-center rounded-full border border-slate-200 text-slate-600 hover:bg-slate-100"
                    onClick={() =>
                      setOpenMenuId((current) => (current === role.id ? null : role.id))
                    }
                  >
                    …
                  </button>
                  {openMenuId === role.id && (
                    <div className="absolute right-0 mt-2 w-40 rounded-xl border border-slate-200 bg-white shadow-md z-10">
                      <button
                        type="button"
                        className="w-full px-3 py-2 text-left text-sm hover:bg-slate-50"
                        onClick={() => handleEdit(role)}
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
                        disabled={deleteMutation.isPending}
                        onClick={() => handleDelete(role)}
                      >
                        {deleteMutation.isPending ? 'Deleting...' : 'Delete'}
                      </button>
                    </div>
                  )}
                </Td>
              </tr>
            ))}

            {jobRoles?.length === 0 && !isLoading && (
              <tr>
                <Td colSpan={visibleColumns.length + 1} className="text-center text-slate-500 py-6">
                  No job roles found.
                </Td>
              </tr>
            )}
          </tbody>
        </Table>
      </div>

      <div className="mt-4 flex items-center justify-between text-sm text-slate-600">
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

      {(selectedRole || isCreateMode) && (
        <JobRoleDetail 
          role={selectedRole ?? undefined} 
          isOpen={!!selectedRole || isCreateMode}
          onClose={handleClose}
          isCreateMode={isCreateMode}
        />
      )}

      <ColumnsPanel
        isOpen={columnsPanelOpen}
        onClose={() => setColumnsPanelOpen(false)}
        columnIds={JOB_ROLE_COLUMN_IDS}
        labels={JOB_ROLE_COLUMN_LABELS}
        currentConfig={columnConfig}
        onSave={handleColumnConfigSave}
      />
    </div>
  );
}
