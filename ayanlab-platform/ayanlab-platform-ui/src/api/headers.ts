const CORRELATION_KEY = "ayanlabs.correlationId";

export function correlationId(): string {
  let id = sessionStorage.getItem(CORRELATION_KEY);
  if (!id) {
    id = crypto.randomUUID();
    sessionStorage.setItem(CORRELATION_KEY, id);
  }
  return id;
}

export function clientHeaders(tokens: {
  appId?: string;
  accessToken?: string;
  refreshToken?: string;
  profileId?: string;
  orgId?: string;
  requireTokens: boolean;
  requireAppId?: boolean;
}): Record<string, string> {
  const h: Record<string, string> = {
    "X-Correlation-Id": correlationId(),
    "X-Client-Type": "Web",
    "X-Client-Language": "en",
    "Content-Type": "application/json",
  };
  const needApp = tokens.requireAppId !== false;
  if (needApp && tokens.appId) {
    h["X-App-Id"] = tokens.appId;
  }
  if (tokens.profileId) h["X-Profile-Id"] = tokens.profileId;
  if (tokens.orgId) h["X-Org-Id"] = tokens.orgId;
  if (tokens.requireTokens && tokens.accessToken) {
    h["X-Client-Token"] = tokens.accessToken;
    h["Authorization"] = `Bearer ${tokens.accessToken}`;
    if (tokens.refreshToken) {
      h["X-Client-Refresh-Token"] = tokens.refreshToken;
    }
  }
  return h;
}