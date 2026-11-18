/**
 * Profile UI data stored in localStorage (e.g. tax and fee from profile details).
 * Key: profileUiData. When not set, defaults: 8% tax, 10% fee.
 */

const PROFILE_UI_DATA_KEY = 'profileUiData';

export type ProfileUiData = {
  taxPercent?: number;
  feePercent?: number;
};

const DEFAULTS: Required<Pick<ProfileUiData, 'taxPercent' | 'feePercent'>> = {
  taxPercent: 8,
  feePercent: 10,
};

export function getProfileUiData(): ProfileUiData {
  try {
    const raw = localStorage.getItem(PROFILE_UI_DATA_KEY);
    if (!raw) return DEFAULTS;
    const parsed = JSON.parse(raw) as ProfileUiData;
    return {
      taxPercent: typeof parsed.taxPercent === 'number' ? parsed.taxPercent : DEFAULTS.taxPercent,
      feePercent: typeof parsed.feePercent === 'number' ? parsed.feePercent : DEFAULTS.feePercent,
    };
  } catch {
    return DEFAULTS;
  }
}

/** Tax and fee percentages from profile (for billing). Defaults: 8% tax, 10% fee. */
export function getProfileTaxAndFee(): { taxPercent: number; feePercent: number } {
  const data = getProfileUiData();
  return {
    taxPercent: data.taxPercent ?? DEFAULTS.taxPercent,
    feePercent: data.feePercent ?? DEFAULTS.feePercent,
  };
}
