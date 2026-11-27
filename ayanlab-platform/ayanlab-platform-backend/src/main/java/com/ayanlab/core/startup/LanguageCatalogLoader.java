package com.ayanlab.core.startup;

import com.ayanlab.core.condition.AuthSeedSurface;
import com.ayanlab.core.entity.ReferenceLanguage;
import com.ayanlab.core.repository.ReferenceLanguageRepository;
import com.ayanlab.core.util.IdGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@AuthSeedSurface
@Component
@Order(9)
public class LanguageCatalogLoader implements CommandLineRunner {

    private static final int BATCH = 200;

    private final ReferenceLanguageRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${platform.db.load-language-catalog:true}")
    private boolean loadCatalog;

    public LanguageCatalogLoader(ReferenceLanguageRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (!loadCatalog) {
            System.out.println("[LanguageCatalogLoader] Disabled by platform.db.load-language-catalog=false");
            return;
        }

        ClassPathResource res = new ClassPathResource("db/language-catalog.json");
        if (!res.exists()) {
            System.out.println("[LanguageCatalogLoader] db/language-catalog.json not found — skip");
            return;
        }

        int created = 0;
        int updated = 0;
        int skipped = 0;
        List<ReferenceLanguage> newBatch = new ArrayList<>(BATCH);

        try (InputStream is = res.getInputStream()) {
            JsonNode root = objectMapper.readTree(is);
            JsonNode languages = resolveLanguagesArray(root);
            if (languages == null || !languages.isArray() || languages.isEmpty()) {
                System.out.println("[LanguageCatalogLoader] No language entries — skip");
                return;
            }

            boolean emptyTable = repository.count() == 0;

            for (JsonNode node : languages) {
                String code = text(node, "code");
                if (code == null) {
                    code = text(node, "id");
                }
                String name = text(node, "name");
                String nativeName = text(node, "nativeName");
                if (nativeName == null) {
                    nativeName = text(node, "native");
                }

                if (code == null || name == null) {
                    skipped++;
                    continue;
                }
                code = code.trim().toLowerCase();
                if (code.isEmpty()) {
                    skipped++;
                    continue;
                }

                if (!emptyTable) {
                    var existing = repository.findByCode(code);
                    if (existing.isPresent()) {
                        ReferenceLanguage row = existing.get();
                        boolean dirty = false;
                        if (!name.equals(row.getName())) {
                            row.setName(name);
                            dirty = true;
                        }
                        if (nativeName != null && !nativeName.equals(row.getNativeName())) {
                            row.setNativeName(nativeName);
                            dirty = true;
                        }
                        if (dirty) {
                            row.setUpdatedAt(Instant.now());
                            repository.save(row);
                            updated++;
                        }
                        continue;
                    }
                }

                ReferenceLanguage row = ReferenceLanguage.builder()
                    .code(code)
                    .name(name)
                    .nativeName(nativeName)
                    .build();
                row.setIsActive(true);
                row.setCreatedAt(Instant.now());
                row.setUpdatedAt(Instant.now());
                IdGenerator.assignIfAbsent(row);
                newBatch.add(row);
                if (newBatch.size() >= BATCH) {
                    repository.saveAll(newBatch);
                    created += newBatch.size();
                    newBatch.clear();
                }
            }

            if (!newBatch.isEmpty()) {
                repository.saveAll(newBatch);
                created += newBatch.size();
            }

            System.out.printf(
                "[LanguageCatalogLoader] entries=%d created=%d updated=%d skipped=%d%n",
                languages.size(), created, updated, skipped
            );
        } catch (Exception e) {
            System.err.println("[LanguageCatalogLoader] failed: " + e.getMessage());
        }
    }

    private static JsonNode resolveLanguagesArray(JsonNode root) {
        if (root == null || root.isNull()) {
            return null;
        }
        if (root.isArray()) {
            return root;
        }
        JsonNode languages = root.get("languages");
        if (languages != null && languages.isArray()) {
            return languages;
        }
        return null;
    }

    private static String text(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull() ? node.get(field).asText().trim() : null;
    }
}