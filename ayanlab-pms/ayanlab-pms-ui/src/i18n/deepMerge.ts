import type { MessageTree } from "./types";

export function deepMerge(base: MessageTree, patch: MessageTree): MessageTree {
  const out: MessageTree = { ...base };
  for (const key of Object.keys(patch)) {
    const a = base[key];
    const b = patch[key];
    if (typeof b === "string") {
      out[key] = b;
    } else if (typeof b === "object" && b !== null) {
      const baseChild = typeof a === "object" && a !== null ? a : {};
      out[key] = deepMerge(baseChild as MessageTree, b);
    }
  }
  return out;
}