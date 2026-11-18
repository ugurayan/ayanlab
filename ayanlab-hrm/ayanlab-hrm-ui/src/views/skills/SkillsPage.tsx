import React, { useState, useCallback } from 'react';
import { useSkills, useDeleteSkill } from '../../data/hooks/useSkills';
import { Button } from '../../ui/components/Button';
import { useAuth } from '../../state/auth';
import { SkillDetail } from './SkillDetail';
import { LoadingOverlay } from '../../ui/components/LoadingSpinner';
import type { Skill } from '../../api/services/skillsAdmin';
import { Table, Th, Td } from '../../ui/components/Table';
import { formatDateTime } from '../../lib/format';
import { ColumnsPanel } from '../../ui/components/ColumnsPanel';
import {
  loadSkillColumnConfig,
  saveSkillColumnConfig,
  getVisibleColumnsInOrder,
  SKILL_COLUMN_IDS,
  SKILL_COLUMN_LABELS,
  type SkillColumnConfig,
  type SkillColumnId,
} from './columnConfig';
import { Columns2 } from 'lucide-react';

export function SkillsPage() {
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [columnConfig, setColumnConfig] = useState<SkillColumnConfig>(() => loadSkillColumnConfig());
  const [columnsPanelOpen, setColumnsPanelOpen] = useState(false);
  const { data, isLoading, error } = useSkills(page, pageSize);
  const deleteMutation = useDeleteSkill();
  const { has } = useAuth();
  const [selectedSkill, setSelectedSkill] = useState<Skill | null>(null);
  const [isCreateMode, setIsCreateMode] = useState(false);
  const [openMenuId, setOpenMenuId] = useState<string | null>(null);

  const visibleColumns = getVisibleColumnsInOrder(columnConfig);
  const handleColumnConfigSave = useCallback((config: SkillColumnConfig) => {
    saveSkillColumnConfig(config);
    setColumnConfig(config);
  }, []);

  const skills = data?.items ?? [];
  const totalPages = data?.totalPages ?? 0;
  const totalElements = data?.totalElements ?? 0;

  const handleUpdate = (skill: Skill) => {
    setSelectedSkill(skill);
    setIsCreateMode(false);
    setOpenMenuId(null);
  };

  const handleCreateNew = () => {
    setSelectedSkill(null);
    setIsCreateMode(true);
    setOpenMenuId(null);
  };

  const handleDelete = async (skill: Skill) => {
    const confirmed = window.confirm(`Delete skill "${skill.name}"?`);
    if (!confirmed) return;
    try {
      await deleteMutation.mutateAsync(skill.id);
      setOpenMenuId(null);
      alert('Skill deleted successfully.');
    } catch (err: any) {
      console.error('Delete skill failed', err);
      alert(err?.message || 'Failed to delete skill.');
    }
  };

  const handleClose = () => {
    setSelectedSkill(null);
    setIsCreateMode(false);
  };

  const handlePageSizeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const newSize = Number(e.target.value);
    setPageSize(newSize);
    setPage(0);
  };

  function renderCell(skill: Skill, columnId: SkillColumnId) {
    switch (columnId) {
      case 'id':
        return <Td className="text-xs text-slate-500">{skill.id}</Td>;
      case 'name':
        return <Td className="font-semibold">{skill.name}</Td>;
      case 'description':
        return <Td className="text-slate-600">{skill.description || 'No description'}</Td>;
      case 'createdBy':
        return <Td className="text-xs text-slate-500">{skill.createdBy}</Td>;
      case 'createdAt':
        return (
          <Td className="text-xs text-slate-500">
            {skill.createdAt ? formatDateTime(new Date(skill.createdAt).toISOString()) : '-'}
          </Td>
        );
      case 'updatedBy':
        return <Td className="text-xs text-slate-500">{skill.updatedBy || '-'}</Td>;
      case 'updatedAt':
        return (
          <Td className="text-xs text-slate-500">
            {skill.updatedAt ? formatDateTime(new Date(skill.updatedAt).toISOString()) : '-'}
          </Td>
        );
      default:
        return null;
    }
  }

  const thClass = (id: SkillColumnId) => (id === 'id' ? 'w-[220px]' : undefined);

  if (error) return <div className="p-6">Error loading skills</div>;

  return (
    <div className="h-full flex flex-col relative">
      {isLoading && <LoadingOverlay text="Loading skills..." />}
      
      <div className="flex-shrink-0 p-6 pb-4">
        <div className="flex justify-between items-center mb-4">
          <h1 className="text-2xl font-bold">Skills</h1>
          <div className="flex items-center gap-2">
            <Button variant="secondary" onClick={() => setColumnsPanelOpen(true)} className="inline-flex items-center gap-2">
              <Columns2 size={18} />
              Columns
            </Button>
            {has('shift:read') && (
              <Button onClick={handleCreateNew}>
                + Add New Skill
              </Button>
            )}
          </div>
        </div>

        <div className="flex items-center justify-between text-sm text-slate-600">
          <div>
            Total skills: <span className="font-semibold">{totalElements}</span>
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
                      {SKILL_COLUMN_LABELS[id]}
                    </Th>
                  ))}
                  <Th className="text-right w-[80px]">Actions</Th>
                </tr>
              </thead>
              <tbody>
                {skills.map((skill) => (
                  <tr key={skill.id} className="hover:bg-slate-50">
                    {visibleColumns.map((id) => (
                      <React.Fragment key={id}>{renderCell(skill, id)}</React.Fragment>
                    ))}
                    <Td className="text-right relative">
                      <button
                        type="button"
                        className="inline-flex h-8 w-8 items-center justify-center rounded-full border border-slate-200 text-slate-600 hover:bg-slate-100"
                        onClick={() =>
                          setOpenMenuId((current) => (current === skill.id ? null : skill.id))
                        }
                      >
                        …
                      </button>
                      {openMenuId === skill.id && (
                        <div className="absolute right-0 mt-2 w-40 rounded-xl border border-slate-200 bg-white shadow-md z-10">
                          <button
                            type="button"
                            className="w-full px-3 py-2 text-left text-sm hover:bg-slate-50"
                            onClick={() => handleUpdate(skill)}
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
                            onClick={() => handleDelete(skill)}
                            disabled={deleteMutation.isPending}
                          >
                            {deleteMutation.isPending ? 'Deleting...' : 'Delete'}
                          </button>
                        </div>
                      )}
                    </Td>
                  </tr>
                ))}

                {skills.length === 0 && !isLoading && (
                  <tr>
                    <Td colSpan={visibleColumns.length + 1} className="text-center text-slate-500 py-6">
                      No skills found.
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

      {(selectedSkill || isCreateMode) && (
        <SkillDetail 
          skill={selectedSkill ?? undefined}
          isOpen={!!selectedSkill || isCreateMode}
          onClose={handleClose}
          isCreateMode={isCreateMode}
        />
      )}

      <ColumnsPanel
        isOpen={columnsPanelOpen}
        onClose={() => setColumnsPanelOpen(false)}
        columnIds={SKILL_COLUMN_IDS}
        labels={SKILL_COLUMN_LABELS}
        currentConfig={columnConfig}
        onSave={handleColumnConfigSave}
      />
    </div>
  );
}
