package com.ayanlab.core.startup;

import com.ayanlab.core.condition.AuthSeedSurface;
import com.ayanlab.core.entity.BankRoutingInstitution;
import com.ayanlab.core.repository.BankRoutingInstitutionRepository;
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

/**
 * Idempotent seed of US ABA routing → institution name from {@code db/bank-routing-catalog.json}.
 * Accepts either {@code {"banks":[{"routing":"...","name":"..."}]}} or a top-level JSON array.
 */
@AuthSeedSurface
@Component
@Order(9)
public class BankRoutingCatalogLoader implements CommandLineRunner {

    private static final int BATCH = 500;

    private final BankRoutingInstitutionRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${platform.db.load-bank-routing-catalog:true}")
    private boolean loadCatalog;

    public BankRoutingCatalogLoader(BankRoutingInstitutionRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (!loadCatalog) {
            System.out.println("[BankRoutingCatalogLoader] Disabled by platform.db.load-bank-routing-catalog=false");
            return;
        }

        ClassPathResource res = new ClassPathResource("db/bank-routing-catalog.json");
        if (!res.exists()) {
            System.out.println("[BankRoutingCatalogLoader] db/bank-routing-catalog.json not found — skip");
            return;
        }

        int created = 0;
        int updated = 0;
        int skipped = 0;
        List<BankRoutingInstitution> newBatch = new ArrayList<>(BATCH);

        try (InputStream is = res.getInputStream()) {
            JsonNode root = objectMapper.readTree(is);
            JsonNode banks = resolveBanksArray(root);
            if (banks == null || !banks.isArray() || banks.isEmpty()) {
                System.out.println("[BankRoutingCatalogLoader] No bank entries in catalog — skip");
                return;
            }

            boolean emptyTable = repository.count() == 0;

            for (JsonNode node : banks) {
                String routing = text(node, "routing");
                if (routing == null) {
                    routing = text(node, "number");
                }
                String name = text(node, "name");
                if (name == null) {
                    name = text(node, "bank");
                }
                if (routing == null || name == null) {
                    skipped++;
                    continue;
                }
                routing = routing.replaceAll("\\D", "");
                if (routing.length() != 9) {
                    skipped++;
                    continue;
                }
                name = name.trim();
                if (name.isEmpty()) {
                    skipped++;
                    continue;
                }

                if (!emptyTable) {
                    var existing = repository.findByRoutingNumber(routing);
                    if (existing.isPresent()) {
                        BankRoutingInstitution row = existing.get();
                        if (!name.equals(row.getInstitutionName())) {
                            row.setInstitutionName(name);
                            row.setUpdatedAt(Instant.now());
                            repository.save(row);
                            updated++;
                        }
                        continue;
                    }
                }

                BankRoutingInstitution row = BankRoutingInstitution.builder()
                    .routingNumber(routing)
                    .institutionName(name)
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
                "[BankRoutingCatalogLoader] entries=%d created=%d updated=%d skipped=%d%n",
                banks.size(), created, updated, skipped
            );
        } catch (Exception e) {
            System.err.println("[BankRoutingCatalogLoader] failed: " + e.getMessage());
        }
    }

    private static JsonNode resolveBanksArray(JsonNode root) {
        if (root == null || root.isNull()) {
            return null;
        }
        if (root.isArray()) {
            return root;
        }
        JsonNode banks = root.get("banks");
        if (banks != null && banks.isArray()) {
            return banks;
        }
        return root.get("routingInstitutions");
    }

    private static String text(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull() ? node.get(field).asText().trim() : null;
    }
}