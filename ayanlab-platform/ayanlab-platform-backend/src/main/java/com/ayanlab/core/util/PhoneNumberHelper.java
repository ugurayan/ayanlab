package com.ayanlab.core.util;

import com.ayanlab.core.exception.CoreException;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

/**
 * Validates and normalizes phone numbers for any country via libphonenumber.
 * Stores dial country code without '+' (e.g. "1", "90") and E.164 mobile.
 */
public final class PhoneNumberHelper {

    private static final PhoneNumberUtil UTIL = PhoneNumberUtil.getInstance();

    private PhoneNumberHelper() {
    }

    public record ParsedPhone(
            String e164,
            /** Dial code digits without '+', e.g. "1", "90". */
            String countryCode,
            /** ISO 3166-1 alpha-2, e.g. "US", "TR". */
            String countryIso
    ) {
    }

    /**
     * @param phoneOrNational E.164 (+15551234567) or national digits for the given region
     * @param countryIso      ISO region hint (required when phone is national / no '+')
     */
    public static ParsedPhone parseAndValidate(String phoneOrNational, String countryIso) {
        String raw = phoneOrNational == null ? "" : phoneOrNational.trim();
        if (raw.isBlank()) {
            throw new CoreException("VALIDATION_ERROR", "Phone is required");
        }

        String iso = countryIso == null ? "" : countryIso.trim().toUpperCase();
        String cleaned = raw.replaceAll("[\\s().-]", "");

        try {
            Phonenumber.PhoneNumber number;
            if (cleaned.startsWith("+") || cleaned.startsWith("00")) {
                String international = cleaned.startsWith("00") ? "+" + cleaned.substring(2) : cleaned;
                number = UTIL.parse(international, iso.isBlank() ? null : iso);
            } else {
                if (iso.isBlank()) {
                    throw new CoreException("VALIDATION_ERROR", "phoneCountryIso is required for national numbers");
                }
                number = UTIL.parse(cleaned, iso);
            }

            if (!UTIL.isValidNumber(number)) {
                throw new CoreException("VALIDATION_ERROR", "Invalid phone number for the selected country");
            }

            // If UI sent a region, ensure number matches that region when possible
            String region = UTIL.getRegionCodeForNumber(number);
            if (!iso.isBlank() && region != null && !iso.equals(region)) {
                // Still accept if valid for another region sharing dial code only when not forced —
                // prefer strict match when iso provided.
                if (!UTIL.isValidNumberForRegion(number, iso)) {
                    throw new CoreException(
                            "VALIDATION_ERROR",
                            "Phone number is not valid for country " + iso
                    );
                }
                region = iso;
            }

            String e164 = UTIL.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);
            String dial = String.valueOf(number.getCountryCode());
            if (region == null || region.isBlank()) {
                region = iso.isBlank() ? null : iso;
            }
            return new ParsedPhone(e164, dial, region);
        } catch (NumberParseException e) {
            throw new CoreException("VALIDATION_ERROR", "Invalid phone number format");
        }
    }
}
