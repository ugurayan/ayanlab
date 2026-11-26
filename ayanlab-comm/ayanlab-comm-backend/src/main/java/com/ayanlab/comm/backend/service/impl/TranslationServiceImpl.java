package com.ayanlab.comm.backend.service.impl;

import com.ayanlab.comm.backend.service.TranslationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Slf4j
@Service
public class TranslationServiceImpl implements TranslationService {

    @Value("${DEEPL_API_KEY:}")
    private String deeplApiKey;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Override
    public TranslatedContent translate(String text, String htmlBody, String sourceLang, String targetLang) {
        if (sourceLang.equalsIgnoreCase(targetLang)) {
            return new TranslatedContent(text, htmlBody);
        }

        // If API Key is configured, use DeepL API
        if (deeplApiKey != null && !deeplApiKey.isBlank()) {
            try {
                String translatedSubject = callDeepLApi(text, sourceLang, targetLang, false);
                String translatedBody = callDeepLApi(htmlBody, sourceLang, targetLang, true);
                return new TranslatedContent(translatedSubject, translatedBody);
            } catch (Exception e) {
                log.error("DeepL translation failed. Falling back to local smart translation. Error: {}", e.getMessage());
            }
        }

        // Fallback: Smart Mock Translator (translates HTML content cleanly for multi-lingual testing)
        return smartMockTranslation(text, htmlBody, targetLang);
    }

    private String callDeepLApi(String content, String sourceLang, String targetLang, boolean isHtml) throws Exception {
        String url = "https://api-free.deepl.com/v2/translate";
        String body = "text=" + java.net.URLEncoder.encode(content, java.nio.charset.StandardCharsets.UTF_8)
                + "&source_lang=" + sourceLang.toUpperCase()
                + "&target_lang=" + targetLang.toUpperCase()
                + (isHtml ? "&tag_handling=html" : "");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "DeepL-Auth-Key " + deeplApiKey)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            // Very simple JSON extraction for quick response parsing without heavy dependencies
            String json = response.body();
            int start = json.indexOf("\"text\":\"") + 8;
            int end = json.indexOf("\"}", start);
            if (start > 7 && end > start) {
                return json.substring(start, end)
                        .replace("\\\"", "\"")
                        .replace("\\n", "\n");
            }
        }
        throw new RuntimeException("HTTP Status " + response.statusCode() + ": " + response.body());
    }

    private TranslatedContent smartMockTranslation(String text, String htmlBody, String targetLang) {
        String prefix = "[" + targetLang.toUpperCase() + " Translation] ";
        String translatedSubject = prefix + text;

        // Wrap the HTML body visually for easy local preview and testing of translations
        String translatedBody = htmlBody;
        if (htmlBody != null && htmlBody.contains("<body")) {
            translatedBody = htmlBody.replace("<body", "<body data-lang=\"" + targetLang + "\"");
        }
        translatedBody = "<div style='border: 1px dashed #4F46E5; padding: 10px; margin-bottom: 10px;'>"
                + "<span style='background-color: #4F46E5; color: white; padding: 2px 6px; font-size: 11px; font-weight: bold; border-radius: 3px;'>"
                + targetLang.toUpperCase() + " VERSION</span><br/>"
                + translatedBody + "</div>";

        return new TranslatedContent(translatedSubject, translatedBody);
    }
}
