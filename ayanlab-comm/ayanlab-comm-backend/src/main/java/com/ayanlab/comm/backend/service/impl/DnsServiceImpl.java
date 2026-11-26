package com.ayanlab.comm.backend.service.impl;

import com.ayanlab.comm.backend.service.DnsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

@Slf4j
@Service
public class DnsServiceImpl implements DnsService {

    @Override
    public Map<String, Boolean> verifyDomainRecords(String domainName, String verificationToken, String dkimSelector) {
        Map<String, Boolean> results = new HashMap<>();
        results.put("spf", false);
        results.put("dkim", false);
        results.put("dmarc", false);

        Hashtable<String, String> env = new Hashtable<>();
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");

        try {
            DirContext ictx = new InitialDirContext(env);

            // 1. Verify SPF (TXT Record on domainName)
            try {
                Attributes attrs = ictx.getAttributes(domainName, new String[]{"TXT"});
                Attribute txtAttr = attrs.get("TXT");
                if (txtAttr != null) {
                    for (int i = 0; i < txtAttr.size(); i++) {
                        String value = txtAttr.get(i).toString();
                        if (value.contains("v=spf1") && (value.contains("include:amazonses.com") || value.contains("ayanlab"))) {
                            results.put("spf", true);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("SPF lookup failed for domain: {}. Error: {}", domainName, e.getMessage());
            }

            // 2. Verify DKIM (CNAME lookup on dkimSelector._domainkey.domainName)
            try {
                String dkimDomain = dkimSelector + "._domainkey." + domainName;
                Attributes attrs = ictx.getAttributes(dkimDomain, new String[]{"CNAME", "TXT"});
                Attribute cnameAttr = attrs.get("CNAME");
                Attribute txtAttr = attrs.get("TXT");
                if (cnameAttr != null || txtAttr != null) {
                    results.put("dkim", true);
                }
            } catch (Exception e) {
                log.warn("DKIM lookup failed for domain: {}. Error: {}", domainName, e.getMessage());
            }

            // 3. Verify DMARC (TXT Record on _dmarc.domainName)
            try {
                String dmarcDomain = "_dmarc." + domainName;
                Attributes attrs = ictx.getAttributes(dmarcDomain, new String[]{"TXT"});
                Attribute txtAttr = attrs.get("TXT");
                if (txtAttr != null) {
                    for (int i = 0; i < txtAttr.size(); i++) {
                        String value = txtAttr.get(i).toString();
                        if (value.contains("v=DMARC1")) {
                            results.put("dmarc", true);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("DMARC lookup failed for domain: {}. Error: {}", domainName, e.getMessage());
            }

        } catch (Exception e) {
            log.error("DNS verification failed for domain: {}. General error: {}", domainName, e.getMessage());
        }

        return results;
    }
}
