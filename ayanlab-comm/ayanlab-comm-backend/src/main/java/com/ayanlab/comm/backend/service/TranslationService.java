package com.ayanlab.comm.backend.service;

public interface TranslationService {
    /**
     * Translates a given subject and HTML body from a source language to a target language.
     * Uses DeepL or LLM API if configured; falls back to a smart mock translation for local dev.
     */
    TranslatedContent translate(String text, String htmlBody, String sourceLang, String targetLang);

    record TranslatedContent(String subject, String bodyHtml) {}
}
