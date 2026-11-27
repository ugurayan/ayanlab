package com.ayanlab.core.startup;

import com.ayanlab.core.condition.AuthSeedSurface;
import com.ayanlab.core.entity.ReferenceCountry;
import com.ayanlab.core.repository.ReferenceCountryRepository;
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
public class CountryCatalogLoader implements CommandLineRunner {

    private static final int BATCH = 200;

    private final ReferenceCountryRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${platform.db.load-country-catalog:true}")
    private boolean loadCatalog;

    public CountryCatalogLoader(ReferenceCountryRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (!loadCatalog) {
            System.out.println("[CountryCatalogLoader] Disabled by platform.db.load-country-catalog=false");
            return;
        }

        ClassPathResource res = new ClassPathResource("db/country-catalog.json");
        if (!res.exists()) {
            System.out.println("[CountryCatalogLoader] db/country-catalog.json not found — skip");
            return;
        }

        int created = 0;
        int updated = 0;
        int skipped = 0;
        List<ReferenceCountry> newBatch = new ArrayList<>(BATCH);

        try (InputStream is = res.getInputStream()) {
            JsonNode root = objectMapper.readTree(is);
            JsonNode countries = resolveCountriesArray(root);
            if (countries == null || !countries.isArray() || countries.isEmpty()) {
                System.out.println("[CountryCatalogLoader] No country entries — skip");
                return;
            }

            boolean emptyTable = repository.count() == 0;

            for (JsonNode node : countries) {
                String alpha2 = text(node, "alpha2Code");
                if (alpha2 == null) {
                    alpha2 = text(node, "id");
                }
                String name = text(node, "name");
                String alpha3 = text(node, "alpha3Code");
                String numeric = text(node, "countryCode");
                if (numeric == null) {
                    numeric = text(node, "numericCode");
                }
                String region = text(node, "region");

                if (alpha2 == null || name == null || alpha3 == null) {
                    skipped++;
                    continue;
                }
                alpha2 = alpha2.trim().toUpperCase();
                alpha3 = alpha3.trim().toUpperCase();
                if (alpha2.length() != 2 || alpha3.length() != 3) {
                    skipped++;
                    continue;
                }
                if (region != null && region.isEmpty()) {
                    region = null;
                }

                if (!emptyTable) {
                    var existing = repository.findByAlpha2Code(alpha2);
                    if (existing.isPresent()) {
                        ReferenceCountry row = existing.get();
                        boolean dirty = false;
                        if (!name.equals(row.getName())) {
                            row.setName(name);
                            dirty = true;
                        }
                        if (!alpha3.equals(row.getAlpha3Code())) {
                            row.setAlpha3Code(alpha3);
                            dirty = true;
                        }
                        if (numeric != null && !numeric.equals(row.getNumericCode())) {
                            row.setNumericCode(numeric);
                            dirty = true;
                        }
                        if (region == null && row.getRegion() != null) {
                            row.setRegion(null);
                            dirty = true;
                        } else if (region != null && !region.equals(row.getRegion())) {
                            row.setRegion(region);
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

                ReferenceCountry row = ReferenceCountry.builder()
                    .alpha2Code(alpha2)
                    .name(name)
                    .alpha3Code(alpha3)
                    .numericCode(numeric)
                    .region(region)
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
                "[CountryCatalogLoader] entries=%d created=%d updated=%d skipped=%d%n",
                countries.size(), created, updated, skipped
            );
        } catch (Exception e) {
            System.err.println("[CountryCatalogLoader] failed: " + e.getMessage());
        }
    }

    private static JsonNode resolveCountriesArray(JsonNode root) {
        if (root == null || root.isNull()) {
            return null;
        }
        if (root.isArray()) {
            return root;
        }
        JsonNode countries = root.get("countries");
        if (countries != null && countries.isArray()) {
            return countries;
        }
        return null;
    }

    private static String text(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull() ? node.get(field).asText().trim() : null;
    }
}