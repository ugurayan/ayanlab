package com.ayanlab.comm.backend.service.impl;

import com.ayanlab.comm.backend.dto.CampaignCreateRequest;
import com.ayanlab.comm.backend.entity.*;
import com.ayanlab.comm.backend.event.CommEmailDispatchEvent;
import com.ayanlab.comm.backend.repository.*;
import com.ayanlab.comm.backend.service.CampaignService;
import com.ayanlab.comm.backend.service.TranslationService;
import com.ayanlab.comm.backend.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {

    private final CampaignRepository campaignRepository;
    private final CampaignTranslationRepository campaignTranslationRepository;
    private final EmailTemplateRepository templateRepository;
    private final EmailTemplateTranslationRepository templateTranslationRepository;
    private final ContactListRepository contactListRepository;
    private final EmailOutboxRepository outboxRepository;
    private final SuppressionListRepository suppressionRepository;
    private final TranslationService translationService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // The 11 supported system languages (from golden mobile-template spec)
    private static final List<String> SYSTEM_LANGUAGES = List.of("en", "zh", "hi", "es", "fr", "ar", "bn", "pt", "ru", "tr", "ur");

    @Override
    @Transactional
    public Campaign createCampaign(UUID organizationId, UUID appId, CampaignCreateRequest request) {
        log.info("Creating campaign: {} for organization: {}", request.name(), organizationId);

        EmailTemplate template = templateRepository.findById(request.templateId())
                .orElseThrow(() -> new IllegalArgumentException("Template not found"));

        ContactList targetList = contactListRepository.findById(request.targetListId())
                .orElseThrow(() -> new IllegalArgumentException("Contact List not found"));

        Campaign campaign = new Campaign();
        campaign.setOrganizationId(organizationId);
        campaign.setAppId(appId);
        campaign.setName(request.name());
        campaign.setTemplate(template);
        campaign.setTargetList(targetList);
        campaign.setDeliveryMode(request.deliveryMode() != null ? request.deliveryMode() : "RECIPIENT_PREFERRED");
        campaign.setStatus("DRAFT");

        IdGenerator.assignIfAbsent(campaign);
        Campaign saved = campaignRepository.save(campaign);

        // Auto translate immediately on creation in a non-blocking way
        autoTranslateCampaign(saved.getId());

        return saved;
    }

    @Override
    @Transactional
    public void autoTranslateCampaign(UUID campaignId) {
        log.info("Auto translating campaign ID: {}", campaignId);
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new IllegalArgumentException("Campaign not found"));

        EmailTemplate template = campaign.getTemplate();
        String defaultLang = template.getDefaultLanguage();

        // Get default language translation
        EmailTemplateTranslation defaultTranslation = templateTranslationRepository
                .findByTemplateIdAndLanguage(template.getId(), defaultLang)
                .orElseThrow(() -> new IllegalStateException("Default language translation not found for template"));

        // Create campaign default translation
        CampaignTranslation baseTrans = new CampaignTranslation();
        baseTrans.setCampaign(campaign);
        baseTrans.setLanguage(defaultLang);
        baseTrans.setSubject(defaultTranslation.getSubject());
        baseTrans.setBodyHtml(defaultTranslation.getBodyHtml());
        baseTrans.setStatus("APPROVED"); // Default is always pre-approved
        IdGenerator.assignIfAbsent(baseTrans);
        campaignTranslationRepository.save(baseTrans);

        // Translate to other 10 system languages
        for (String lang : SYSTEM_LANGUAGES) {
            if (lang.equalsIgnoreCase(defaultLang)) continue;

            try {
                TranslationService.TranslatedContent translated = translationService.translate(
                        defaultTranslation.getSubject(),
                        defaultTranslation.getBodyHtml(),
                        defaultLang,
                        lang
                );

                CampaignTranslation trans = new CampaignTranslation();
                trans.setCampaign(campaign);
                trans.setLanguage(lang);
                trans.setSubject(translated.subject());
                trans.setBodyHtml(translated.bodyHtml());
                trans.setStatus("DRAFT"); // Requires teacher preview & approval

                IdGenerator.assignIfAbsent(trans);
                campaignTranslationRepository.save(trans);
            } catch (Exception e) {
                log.error("Failed to translate campaign {} to language {}: {}", campaignId, lang, e.getMessage());
            }
        }

        campaign.setStatus("READY");
        campaignRepository.save(campaign);
    }

    @Override
    @Transactional
    public CampaignTranslation updateCampaignTranslation(UUID campaignId, String language, String subject, String bodyHtml) {
        log.info("Updating translation manual review for campaign: {} [{}]", campaignId, language);
        CampaignTranslation trans = campaignTranslationRepository.findByCampaignIdAndLanguage(campaignId, language)
                .orElseThrow(() -> new IllegalArgumentException("Translation not found for language: " + language));

        trans.setSubject(subject);
        trans.setBodyHtml(bodyHtml);
        trans.setStatus("APPROVED"); // If manually edited, we auto approve
        return campaignTranslationRepository.save(trans);
    }

    @Override
    @Transactional
    public void approveCampaignTranslation(UUID campaignId, String language) {
        log.info("Approving translation for campaign: {} [{}]", campaignId, language);
        CampaignTranslation trans = campaignTranslationRepository.findByCampaignIdAndLanguage(campaignId, language)
                .orElseThrow(() -> new IllegalArgumentException("Translation not found"));

        trans.setStatus("APPROVED");
        campaignTranslationRepository.save(trans);
    }

    @Override
    @Transactional
    public void executeCampaign(UUID campaignId) {
        log.info("Executing and dispatching campaign ID: {}", campaignId);
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new IllegalArgumentException("Campaign not found"));

        if ("SENDING".equals(campaign.getStatus()) || "COMPLETED".equals(campaign.getStatus())) {
            throw new IllegalStateException("Campaign is already sending or completed.");
        }

        campaign.setStatus("SENDING");
        campaignRepository.save(campaign);

        ContactList list = campaign.getTargetList();
        Set<Contact> recipients = list.getContacts();

        // Get all approved translations for this campaign
        List<CampaignTranslation> approvedTranslations = campaignTranslationRepository.findByCampaignId(campaignId)
                .stream().filter(t -> "APPROVED".equals(t.getStatus())).toList();

        Map<String, CampaignTranslation> translationMap = new HashMap<>();
        for (CampaignTranslation t : approvedTranslations) {
            translationMap.put(t.getLanguage(), t);
        }

        String defaultLang = campaign.getTemplate().getDefaultLanguage();
        CampaignTranslation defaultTrans = translationMap.get(defaultLang);

        if (defaultTrans == null) {
            throw new IllegalStateException("Default language approved translation is missing for the campaign.");
        }

        for (Contact recipient : recipients) {
            // Protection: Check suppression list (spam & hard bounces)
            if (suppressionRepository.existsByOrganizationIdAndEmail(campaign.getOrganizationId(), recipient.getEmail())) {
                log.warn("Recipient {} is suppressed. Skipping delivery.", recipient.getEmail());
                continue;
            }

            // Render multilingual HTML output based on campaign Delivery Mode
            RenderedMail rendered = renderMultilingualMail(recipient, campaign.getDeliveryMode(), defaultLang, translationMap);

            EmailOutbox outbox = new EmailOutbox();
            outbox.setCampaignId(campaignId);
            outbox.setOrganizationId(campaign.getOrganizationId());
            outbox.setRecipientEmail(recipient.getEmail());
            outbox.setRecipientContactId(recipient.getId());
            outbox.setSubject(rendered.subject());
            outbox.setBodyHtml(rendered.bodyHtml());
            outbox.setStatus("PENDING");

            IdGenerator.assignIfAbsent(outbox);
            outboxRepository.save(outbox);

            // Publish to Kafka Queue (Resilient non-blocking delivery)
            try {
                CommEmailDispatchEvent event = new CommEmailDispatchEvent(outbox.getId(), campaignId, recipient.getEmail());
                kafkaTemplate.send("comm-email-dispatch", outbox.getId().toString(), event);
            } catch (Exception e) {
                log.error("Failed to publish email dispatch event to Kafka for {}: {}", recipient.getEmail(), e.getMessage());
            }
        }

        campaign.setStatus("COMPLETED");
        campaign.setSentAt(Instant.now());
        campaignRepository.save(campaign);
        log.info("Campaign ID: {} has been queued for dispatch successfully.", campaignId);
    }

    private record RenderedMail(String subject, String bodyHtml) {}

    private RenderedMail renderMultilingualMail(Contact recipient, String deliveryMode, String defaultLang, Map<String, CampaignTranslation> translationMap) {
        String preferredLang = recipient.getPreferredLanguage();
        CampaignTranslation preferredTrans = translationMap.get(preferredLang);
        CampaignTranslation defaultTrans = translationMap.get(defaultLang);

        // Fallback if preferred language is not approved
        if (preferredTrans == null) {
            preferredTrans = defaultTrans;
        }

        if ("RECIPIENT_PREFERRED".equalsIgnoreCase(deliveryMode)) {
            return new RenderedMail(preferredTrans.getSubject(), preferredTrans.getBodyHtml());
        }

        if ("BILINGUAL_STACK".equalsIgnoreCase(deliveryMode)) {
            if (preferredLang.equalsIgnoreCase(defaultLang)) {
                return new RenderedMail(defaultTrans.getSubject(), defaultTrans.getBodyHtml());
            }

            // Visually Stack Default Language + Recipient Preferred Language
            String bilingualSubject = defaultTrans.getSubject() + " / " + preferredTrans.getSubject();
            String bilingualBody = "<div style='font-family: sans-serif;'>"
                    + "<div class='default-version' style='margin-bottom: 30px;'>"
                    + defaultTrans.getBodyHtml()
                    + "</div>"
                    + "<hr style='border: 0; border-top: 1px dashed #D1D5DB; margin: 30px 0;'/>"
                    + "<div class='preferred-version' style='color: #374151;'>"
                    + "<p style='font-size: 12px; color: #9CA3AF; margin-bottom: 10px;'>--- Translation (" + preferredLang.toUpperCase() + ") ---</p>"
                    + preferredTrans.getBodyHtml()
                    + "</div>"
                    + "</div>";
            return new RenderedMail(bilingualSubject, bilingualBody);
        }

        if ("ALL_STACK".equalsIgnoreCase(deliveryMode)) {
            StringBuilder subjects = new StringBuilder();
            StringBuilder bodyBuilder = new StringBuilder("<div style='font-family: sans-serif;'>");

            boolean first = true;
            for (Map.Entry<String, CampaignTranslation> entry : translationMap.entrySet()) {
                if (!first) {
                    subjects.append(" / ");
                    bodyBuilder.append("<hr style='border: 0; border-top: 1px dashed #D1D5DB; margin: 30px 0;'/>");
                }
                first = false;
                subjects.append(entry.getValue().getSubject());

                bodyBuilder.append("<div style='margin-bottom: 20px;'>")
                        .append("<p style='font-size: 11px; color: #3B82F6; font-weight: bold; margin: 0 0 10px 0;'>")
                        .append(entry.getKey().toUpperCase()).append(" VERSION</p>")
                        .append(entry.getValue().getBodyHtml())
                        .append("</div>");
            }
            bodyBuilder.append("</div>");
            return new RenderedMail(subjects.toString(), bodyBuilder.toString());
        }

        return new RenderedMail(defaultTrans.getSubject(), defaultTrans.getBodyHtml());
    }

    @Override
    @Transactional(readOnly = true)
    public Campaign getCampaignDetails(UUID campaignId) {
        return campaignRepository.findById(campaignId)
                .orElseThrow(() -> new IllegalArgumentException("Campaign not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Campaign> listCampaigns(UUID organizationId) {
        return campaignRepository.findByOrganizationId(organizationId);
    }
}
