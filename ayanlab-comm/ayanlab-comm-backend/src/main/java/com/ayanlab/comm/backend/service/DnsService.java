package com.ayanlab.comm.backend.service;

import java.util.Map;
import java.util.UUID;

public interface DnsService {
    /**
     * Performs actual DNS lookup to verify SPF, DKIM, and DMARC records for a domain.
     * Returns a map containing verification results: "spf", "dkim", "dmarc" mapped to Booleans.
     */
    Map<String, Boolean> verifyDomainRecords(String domainName, String verificationToken, String dkimSelector);
}
