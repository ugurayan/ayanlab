import React, { useState, useCallback, useEffect } from 'react';
import { GripVertical } from 'lucide-react';
import { Modal } from './Modal';
import { Button } from './Button';
import type { TableColumnConfig } from '../../lib/tableColumnConfig';

type ColumnsPanelProps<ColumnId extends string> = {
  isOpen: boolean;
  onClose: () => void;
  columnIds: readonly ColumnId[];
  labels: Record<ColumnId, string>;
  currentConfig: TableColumnConfig<ColumnId>;
  onSave: (config: TableColumnConfig<ColumnId>) => void;
  title?: string;
};

export function ColumnsPanel<ColumnId extends string>({
  isOpen,
  onClose,
  columnIds,
  labels,
  currentConfig,
  onSave,
  title = 'Table columns',
}: ColumnsPanelProps<ColumnId>) {
  const [visibleIds, setVisibleIds] = useState<Set<ColumnId>>(() => new Set(currentConfig.visibleIds));
  const [order, setOrder] = useState<ColumnId[]>(currentConfig.order);

  useEffect(() => {
    if (isOpen) {
      setVisibleIds(new Set(currentConfig.visibleIds));
      setOrder(currentConfig.order);
    }
  }, [isOpen, currentConfig.visibleIds, currentConfig.order]);

  const toggleVisible = useCallback((id: ColumnId) => {
    setVisibleIds((prev) => {
      const next = new Set(prev);
      if (next.has(id)) next.delete(id);
      else next.add(id);
      return next;
    });
  }, []);

  const handleDragStart = useCallback((e: React.DragEvent, index: number) => {
    e.dataTransfer.effectAllowed = 'move';
    e.dataTransfer.setData('text/plain', String(index));
    e.dataTransfer.setData('application/json', JSON.stringify({ index }));
  }, []);

  const handleDragOver = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    e.dataTransfer.dropEffect = 'move';
  }, []);

  const handleDrop = useCallback((e: React.DragEvent, dropIndex: number) => {
    e.preventDefault();
    const raw = e.dataTransfer.getData('application/json') ?? e.dataTransfer.getData('text/plain');
    let dragIndex: number;
    try {
      const parsed = JSON.parse(raw);
      dragIndex = typeof parsed?.index === 'number' ? parsed.index : parseInt(raw, 10);
    } catch {
      dragIndex = parseInt(raw, 10);
    }
    if (Number.isNaN(dragIndex) || dragIndex === dropIndex) return;
    setOrder((prev) => {
      const next = [...prev];
      const [removed] = next.splice(dragIndex, 1);
      next.splice(dropIndex, 0, removed);
      return next;
    });
  }, []);

  const handleSave = useCallback(() => {
    onSave({
      visibleIds: Array.from(visibleIds),
      order: [...order],
    });
    onClose();
  }, [visibleIds, order, onSave, onClose]);

  const handleReset = useCallback(() => {
    const allIds = [...columnIds];
    setVisibleIds(new Set(allIds));
    setOrder(allIds);
  }, [columnIds]);

  const orderedIds =
    order.length === columnIds.length
      ? order
      : [...order, ...columnIds.filter((id) => !order.includes(id))];

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={title} size="md">
      <div className="space-y-4">
        <p className="text-sm text-slate-600">
          Choose which columns to show and drag to reorder. Changes are saved when you click Save.
        </p>

        <div>
          <span className="text-sm font-medium text-slate-700 block mb-2">
            Visible columns (drag to reorder)
          </span>
          <ul className="border border-slate-200 rounded-xl divide-y divide-slate-100 overflow-hidden bg-slate-50/50">
            {orderedIds.map((id, index) => (
              <li
                key={id}
                draggable
                onDragStart={(e) => handleDragStart(e, index)}
                onDragOver={handleDragOver}
                onDrop={(e) => handleDrop(e, index)}
                className="flex items-center gap-2 px-3 py-2 bg-white hover:bg-slate-50 transition-colors cursor-grab active:cursor-grabbing"
              >
                <span
                  className="text-slate-400 hover:text-slate-600 touch-none"
                  aria-hidden
                  onPointerDown={(e) => e.preventDefault()}
                >
                  <GripVertical size={16} />
                </span>
                <label className="flex-1 flex items-center gap-2 cursor-pointer py-0.5">
                  <input
                    type="checkbox"
                    checked={visibleIds.has(id)}
                    onChange={() => toggleVisible(id)}
                    className="rounded border-slate-300 text-slate-900 focus:ring-slate-500"
                  />
                  <span className="text-sm font-medium text-slate-800">{labels[id]}</span>
                </label>
              </li>
            ))}
          </ul>
        </div>

        <div className="flex flex-wrap gap-2 pt-2">
          <Button onClick={handleSave}>Save</Button>
          <Button variant="secondary" onClick={handleReset}>
            Reset to default
          </Button>
          <Button variant="ghost" onClick={onClose}>
            Cancel
          </Button>
        </div>
      </div>
    </Modal>
  );
}
