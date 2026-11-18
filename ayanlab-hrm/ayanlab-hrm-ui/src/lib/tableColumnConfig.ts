/**
 * Generic table column config: which columns are visible and in what order.
 * Persisted to localStorage via the provided storage key.
 */
export type TableColumnConfig<ColumnId extends string> = {
  visibleIds: ColumnId[];
  order: ColumnId[];
};

export function createTableColumnConfig<ColumnId extends string>(
  storageKey: string,
  columnIds: readonly ColumnId[],
  labels: Record<ColumnId, string>
) {
  const allIds = [...columnIds];
  const defaultConfig: TableColumnConfig<ColumnId> = {
    visibleIds: allIds,
    order: allIds,
  };

  function parseStored(value: string | null): TableColumnConfig<ColumnId> | null {
    if (!value) return null;
    try {
      const raw = JSON.parse(value) as unknown;
      const obj = raw as { visibleIds?: unknown; order?: unknown } | null;
      if (obj && typeof obj === 'object' && Array.isArray(obj.visibleIds) && Array.isArray(obj.order)) {
        const visibleIds = obj.visibleIds.filter((id: string) =>
          columnIds.includes(id as ColumnId)
        ) as ColumnId[];
        const order = obj.order.filter((id: string) =>
          columnIds.includes(id as ColumnId)
        ) as ColumnId[];
        if (visibleIds.length > 0 && order.length > 0) {
          return { visibleIds, order };
        }
      }
    } catch {
      // ignore
    }
    return null;
  }

  function load(): TableColumnConfig<ColumnId> {
    if (typeof window === 'undefined') return defaultConfig;
    const stored = window.localStorage.getItem(storageKey);
    const parsed = parseStored(stored);
    return parsed ?? defaultConfig;
  }

  function save(config: TableColumnConfig<ColumnId>): void {
    if (typeof window === 'undefined') return;
    window.localStorage.setItem(storageKey, JSON.stringify(config));
  }

  function getVisibleColumnsInOrder(config: TableColumnConfig<ColumnId>): ColumnId[] {
    return config.order.filter((id) => config.visibleIds.includes(id));
  }

  return {
    columnIds,
    labels,
    defaultConfig,
    load,
    save,
    getVisibleColumnsInOrder,
  };
}
