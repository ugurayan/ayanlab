import { useEffect, useRef, useState } from "react";

export const MIN_PAGE_LOADER_MS = 500;

/** Keeps the page loader visible until `busy` is false and at least 1s have elapsed. */
export function useMinimumLoading(busy: boolean, minMs = MIN_PAGE_LOADER_MS, scope?: string): boolean {
  const [visible, setVisible] = useState(busy);
  const startedAt = useRef(busy ? Date.now() : 0);

  useEffect(() => {
    if (busy) {
      startedAt.current = Date.now();
      setVisible(true);
      return;
    }
    const remaining = minMs - (Date.now() - startedAt.current);
    if (remaining <= 0) {
      setVisible(false);
      return;
    }
    const id = window.setTimeout(() => setVisible(false), remaining);
    return () => window.clearTimeout(id);
  }, [busy, minMs, scope]);

  return visible;
}

export function PageLoader({ label = "Loading" }: { label?: string }) {
  return (
    <div className="page-loader" role="status" aria-live="polite">
      <div className="ayan-mark" aria-hidden>
        <span>A</span>
      </div>
      <p className="ayan-word">
        <span>Ayan</span>
        <span className="ayan-lab">Lab</span>
      </p>
      <p className="muted">{label}</p>
    </div>
  );
}
