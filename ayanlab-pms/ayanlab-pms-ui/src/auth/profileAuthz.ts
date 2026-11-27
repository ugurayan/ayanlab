export type ProfileAuthzSnapshot = {
  profileId: string;
  permissions: string[];
  roles: string[];
  policyGroups: string[];
  authzVersion?: string;
  expiresAt: string;
};

function storageKey(prefix: string, profileId: string) {
  return `${prefix}:${profileId}`;
}

export function readProfileAuthz(prefix: string, profileId: string): ProfileAuthzSnapshot | null {
  try {
    const raw = sessionStorage.getItem(storageKey(prefix, profileId));
    if (!raw) return null;
    const parsed = JSON.parse(raw) as ProfileAuthzSnapshot;
    if (!parsed.expiresAt || Date.parse(parsed.expiresAt) <= Date.now()) {
      sessionStorage.removeItem(storageKey(prefix, profileId));
      return null;
    }
    return parsed;
  } catch {
    return null;
  }
}

export function writeProfileAuthz(prefix: string, snapshot: ProfileAuthzSnapshot) {
  if (!snapshot.profileId) return;
  sessionStorage.setItem(storageKey(prefix, snapshot.profileId), JSON.stringify(snapshot));
  sessionStorage.setItem(`${prefix}:current`, snapshot.profileId);
}

export async function ensureProfileAuthz(
  prefix: string,
  profileId: string | undefined,
  fetchSnapshot: () => Promise<ProfileAuthzSnapshot | null>,
): Promise<ProfileAuthzSnapshot | null> {
  const id = profileId ?? sessionStorage.getItem(`${prefix}:current`) ?? undefined;
  if (id) {
    const cached = readProfileAuthz(prefix, id);
    if (cached) return cached;
  }
  try {
    const fresh = await fetchSnapshot();
    if (fresh?.profileId && fresh.expiresAt) {
      writeProfileAuthz(prefix, fresh);
      return fresh;
    }
  } catch {
    return null;
  }
  return null;
}

export function clearProfileAuthz(prefix: string) {
  const marker = `${prefix}:`;
  for (let i = sessionStorage.length - 1; i >= 0; i -= 1) {
    const key = sessionStorage.key(i);
    if (key?.startsWith(marker)) sessionStorage.removeItem(key);
  }
}
