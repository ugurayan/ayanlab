import type { UiTranslationLanguage } from '../../../../state/i18n';

export const CONTRACT_TYPE_TRANSLATIONS: Record<UiTranslationLanguage, Record<string, string>> = {
  en: {
    '1099': '1099 (Independent Contractor)',
    'w2_permanent': 'W-2 Permanent Employee',
    'w2_temporary': 'W-2 Temporary Employee',
    'part_time': 'Part-Time',
    'full_time': 'Full-Time',
    'seasonal': 'Seasonal',
    'internship': 'Internship',
    'apprenticeship': 'Apprenticeship',
    'per_diem': 'Per Diem',
    'on_call': 'On-Call',
    'temp_to_hire': 'Temp-to-Hire',
  },
  de: {
    '1099': '1099 (Freiberufler)',
    'w2_permanent': 'W-2 Festanstellung',
    'w2_temporary': 'W-2 Zeitarbeit',
    'part_time': 'Teilzeit',
    'full_time': 'Vollzeit',
    'seasonal': 'Saisonarbeit',
    'internship': 'Praktikum',
    'apprenticeship': 'Ausbildung',
    'per_diem': 'Tageweise',
    'on_call': 'Auf Abruf',
    'temp_to_hire': 'Zeitarbeit mit Option auf Festanstellung',
  },
  tr: {
    '1099': '1099 (Bağımsız Yüklenici)',
    'w2_permanent': 'W-2 Sürekli Çalışan',
    'w2_temporary': 'W-2 Geçici Çalışan',
    'part_time': 'Yarı Zamanlı',
    'full_time': 'Tam Zamanlı',
    'seasonal': 'Mevsimlik',
    'internship': 'Staj',
    'apprenticeship': 'Çıraklık',
    'per_diem': 'Günlük',
    'on_call': 'Çağrı Üzerine',
    'temp_to_hire': 'Geçici İşe Alma',
  },
};

export function getContractTypeName(id: string, language: UiTranslationLanguage): string {
  return CONTRACT_TYPE_TRANSLATIONS[language]?.[id] || id;
}
