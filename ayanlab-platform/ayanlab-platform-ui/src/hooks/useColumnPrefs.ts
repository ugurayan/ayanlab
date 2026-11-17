import { useCallback, useEffect, useState } from "react";

const PREFIX = "ayanlab.table.columns.v2.";

function read(entityId: string, fallback: string[]): string[] {
  try {
    const raw = localStorage.getItem(PREFIX + entityId);
    if (!raw) return fallback;
    const parsed = JSON.parse(raw) as unknown;
    if (Array.isArray(parsed) && parsed.every((x) => typeof x === "string") && parsed.length > 0) {
      if (fallback[0] === "id") {
        return ["id", ...parsed.filter((c) => c !== "id")];
      }
      return parsed;
    }
  } catch {
    /* ignore */
  }
  return fallback;
}

export function useColumnPrefs(entityId: string | undefined, defaults: string[]) {
  const key = defaults.join(",");
  const [selected, setSelected] = useState<string[]>(() =>
    entityId ? read(entityId, defaults) : defaults,
  );

  useEffect(() => {
    if (!entityId) return;
    setSelected(read(entityId, defaults));
  }, [entityId, key]);

  const save = useCallback(
    (next: string[]) => {
      const cols = next.length ? next : defaults;
      setSelected(cols);
      if (entityId) localStorage.setItem(PREFIX + entityId, JSON.stringify(cols));
    },
    [entityId, defaults],
  );

  const reset = useCallback(() => {
    setSelected(defaults);
    if (entityId) localStorage.removeItem(PREFIX + entityId);
  }, [entityId, defaults]);

  return { selected, save, reset };
}
