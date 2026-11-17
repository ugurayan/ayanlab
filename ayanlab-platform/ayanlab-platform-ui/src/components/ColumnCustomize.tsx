import { useEffect, useMemo, useState } from "react";
import { humanizeColumn } from "../config/tableColumns";
import { useI18n } from "../i18n/Preferences";

export function ColumnCustomize({
  all,
  selected,
  labels,
  onSave,
  onReset,
  labelOf,
}: {
  all: string[];
  selected: string[];
  labels?: Record<string, string>;
  onSave: (cols: string[]) => void;
  onReset: () => void;
  labelOf?: (key: string) => string;
}) {
  const { t } = useI18n();
  const name = (key: string) => labelOf?.(key) ?? humanizeColumn(key, labels);
  const [open, setOpen] = useState(false);
  const [draft, setDraft] = useState<string[]>(selected);

  useEffect(() => {
    if (open) setDraft(selected);
  }, [open, selected]);

  useEffect(() => {
    if (!open) return;
    const onKey = (e: KeyboardEvent) => {
      if (e.key === "Escape") setOpen(false);
    };
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [open]);

  const ordered = useMemo(() => all, [all]);

  function toggle(key: string) {
    setDraft((cur) => (cur.includes(key) ? cur.filter((c) => c !== key) : [...cur, key]));
  }

  return (
    <div className="customize-wrap">
      <button type="button" className="btn" onClick={() => setOpen(true)}>
        {t("common.customize")}
      </button>
      {open && (
        <div className="sheet-backdrop" onClick={() => setOpen(false)}>
          <div className="sheet" role="dialog" aria-label="Customize columns" onClick={(e) => e.stopPropagation()}>
            <header className="sheet-head">
              <strong>{t("common.columns")}</strong>
              <button type="button" className="icon-btn" onClick={() => setOpen(false)} aria-label="Close">
                ×
              </button>
            </header>
            <p className="muted sheet-hint">{t("common.columnsHint")}</p>
            <ul className="sheet-body">
              {ordered.map((key) => (
                <li key={key}>
                  <label>
                    <input type="checkbox" checked={draft.includes(key)} onChange={() => toggle(key)} />
                    <span>{name(key)}</span>
                    <span className="mono muted">{key}</span>
                  </label>
                </li>
              ))}
            </ul>
            <footer className="sheet-foot">
              <button
                type="button"
                className="btn"
                onClick={() => {
                  onReset();
                  setOpen(false);
                }}
              >
                {t("common.reset")}
              </button>
              <button
                type="button"
                className="btn btn-primary"
                onClick={() => {
                  onSave(draft);
                  setOpen(false);
                }}
              >
                {t("common.apply")}
              </button>
            </footer>
          </div>
        </div>
      )}
    </div>
  );
}
