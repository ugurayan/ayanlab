package com.ayanlab.pms.backend.platform.startup;

import com.ayanlab.core.util.IdGenerator;
import com.ayanlab.pms.backend.enums.PropertyTypeCategoryEnum;
import com.ayanlab.pms.backend.entity.rentals.PropertyType;
import com.ayanlab.pms.backend.repository.PropertyTypeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.Instant;

/**
 * Seeds system-default IPMS property types from property-type-catalog.json.
 * Types with {@code organizationId = null} are visible to all property management organizations.
 */
@Component
@Order(16)
public class IpmsPropertyTypeDataLoader implements CommandLineRunner {

    private final PropertyTypeRepository propertyTypeRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${platform.db.load-ipms-property-types:true}")
    private boolean loadData;

    public IpmsPropertyTypeDataLoader(PropertyTypeRepository propertyTypeRepository) {
        this.propertyTypeRepository = propertyTypeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!loadData) {
            System.out.println("[IpmsPropertyTypeDataLoader] Disabled by platform.db.load-ipms-property-types=false");
            return;
        }

        ClassPathResource res = new ClassPathResource("db/property-type-catalog.json");
        if (!res.exists()) {
            System.out.println("[IpmsPropertyTypeDataLoader] property-type-catalog.json not found");
            return;
        }

        int created = 0;
        int skipped = 0;

        try (InputStream is = res.getInputStream()) {
            JsonNode root = objectMapper.readTree(is);
            JsonNode types = root.get("propertyTypes");
            if (types == null || !types.isArray()) {
                return;
            }

            for (JsonNode node : types) {
                String code = node.get("code").asText();
                if (propertyTypeRepository.findByOrganizationIdIsNullAndCode(code).isPresent()) {
                    skipped++;
                    continue;
                }

                PropertyType type = PropertyType.builder()
                    .organizationId(null)
                    .code(code)
                    .name(node.get("name").asText())
                    .description(textOrNull(node, "description"))
                    .category(PropertyTypeCategoryEnum.valueOf(node.get("category").asText()))
                    .sortOrder(node.has("sortOrder") ? node.get("sortOrder").asInt() : 0)
                    .isSystemDefault(true)
                    .build();
                type.setIsActive(true);
                type.setCreatedAt(Instant.now());
                type.setUpdatedAt(Instant.now());
                IdGenerator.assignIfAbsent(type);
                propertyTypeRepository.save(type);
                created++;
            }
        }

        System.out.println("[IpmsPropertyTypeDataLoader] Summary:");
        System.out.println("  System property types created: " + created);
        System.out.println("  Already present (skipped): " + skipped);
    }

    private static String textOrNull(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull() ? node.get(field).asText() : null;
    }
}
