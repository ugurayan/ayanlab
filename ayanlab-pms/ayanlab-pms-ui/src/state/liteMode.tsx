import React, { createContext, useContext, useEffect, useMemo, useState } from "react";

const KEY = "pms.apsisLite";

type LiteCtx = { lite: boolean; setLite: (v: boolean) => void };

const Ctx = createContext<LiteCtx | undefined>(undefined);

export function LiteModeProvider({ children }: { children: React.ReactNode }) {
  const [lite, setLiteState] = useState(() => localStorage.getItem(KEY) === "1");

  useEffect(() => {
    localStorage.setItem(KEY, lite ? "1" : "0");
  }, [lite]);

  const value = useMemo(
    () => ({
      lite,
      setLite: (v: boolean) => setLiteState(v)
    }),
    [lite]
  );

  return <Ctx.Provider value={value}>{children}</Ctx.Provider>;
}

export function useLiteMode() {
  const ctx = useContext(Ctx);
  if (!ctx) throw new Error("useLiteMode outside LiteModeProvider");
  return ctx;
}