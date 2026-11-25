package com.ayanlab.core.startup;

import com.ayanlab.core.condition.AuthSeedSurface;
import com.ayanlab.core.enums.BillingPeriodEnum;
import com.ayanlab.core.enums.SubscriptionPricingUnitEnum;
import com.ayanlab.core.enums.SubscriptionStatusEnum;
import com.ayanlab.core.entity.*;
import com.ayanlab.core.repository.*;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Loads subscription catalog from {@code db/subscription-data.json} (idempotent).
 * Runs after {@link DefaultDataLoader} so application rows exist.
 */
@AuthSeedSurface
@Component
@Order(15)
public class SubscriptionDataLoader implements CommandLineRunner {

    private final AppRepository appRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionPlanPriceRepository subscriptionPlanPriceRepository;
    private final SubscriptionPlanFeatureRepository subscriptionPlanFeatureRepository;
    private final ProfileSubscriptionRepository profileSubscriptionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${platform.db.load-subscription-data:true}")
    private boolean loadSubscriptionData;

    public SubscriptionDataLoader(
        AppRepository appRepository,
        UserRepository userRepository,
        UserProfileRepository userProfileRepository,
        SubscriptionPlanRepository subscriptionPlanRepository,
        SubscriptionPlanPriceRepository subscriptionPlanPriceRepository,
        SubscriptionPlanFeatureRepository subscriptionPlanFeatureRepository,
        ProfileSubscriptionRepository profileSubscriptionRepository
    ) {
        this.appRepository = appRepository;
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.subscriptionPlanPriceRepository = subscriptionPlanPriceRepository;
        this.subscriptionPlanFeatureRepository = subscriptionPlanFeatureRepository;
        this.profileSubscriptionRepository = profileSubscriptionRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!loadSubscriptionData) {
            System.out.println("[SubscriptionDataLoader] Disabled by platform.db.load-subscription-data=false");
            return;
        }
        if (appRepository.count() == 0) {
            System.out.println("[SubscriptionDataLoader] Skipped: no applications (enable platform.db.load-default-data)");
            return;
        }

        ClassPathResource res = new ClassPathResource("db/subscription-data.json");
        if (!res.exists()) {
            System.out.println("[SubscriptionDataLoader] subscription-data.json not found in classpath");
            return;
        }

        int plansCreated = 0;
        int plansUpdated = 0;
        int pricesCreated = 0;
        int pricesUpdated = 0;
        int featuresCreated = 0;
        int featuresUpdated = 0;
        int profileSubsCreated = 0;

        try (InputStream is = res.getInputStream()) {
            JsonNode root = objectMapper.readTree(is);

            JsonNode catalogs = root.get("catalogs");
            if (catalogs != null && catalogs.isArray()) {
                for (JsonNode catalog : catalogs) {
                    String appCode = catalog.get("appCode").asText();
                    Optional<App> appOpt = appRepository.findByCode(appCode);
                    if (appOpt.isEmpty()) {
                        System.out.println("[SubscriptionDataLoader] Skipped catalog; app not found: " + appCode);
                        continue;
                    }
                    UUID appId = appOpt.get().getId();
                    Map<String, UUID> planCodeToId = new HashMap<>();
                    Map<String, String> planCodeToInheritsCode = new HashMap<>();

                    JsonNode plans = catalog.get("plans");
                    if (plans == null || !plans.isArray()) {
                        continue;
                    }

                    for (JsonNode planNode : plans) {
                        String code = planNode.get("code").asText();
                        if (planNode.has("inheritsFromPlanCode") && !planNode.get("inheritsFromPlanCode").isNull()) {
                            planCodeToInheritsCode.put(code, planNode.get("inheritsFromPlanCode").asText());
                        }

                        SubscriptionPlan plan = subscriptionPlanRepository.findByAppIdAndCode(appId, code)
                            .orElse(null);
                        if (plan == null) {
                            plan = SubscriptionPlan.builder()
                                .appId(appId)
                                .code(code)
                                .name(planNode.get("name").asText())
                                .description(textOrNull(planNode, "description"))
                                .sortOrder(planNode.has("sortOrder") ? planNode.get("sortOrder").asInt() : 0)
                                .isFree(planNode.has("isFree") && planNode.get("isFree").asBoolean())
                                .pricingUnit(parsePricingUnit(planNode))
                                .maxSeats(planNode.has("maxSeats") && !planNode.get("maxSeats").isNull()
                                    ? planNode.get("maxSeats").asInt() : null)
                                .trialDays(planNode.has("trialDays") ? planNode.get("trialDays").asInt() : 0)
                                .catalogActive(true)
                                .build();
                            assignId(plan, planNode);
                            IdGenerator.assignIfAbsent(plan);
                            plan = subscriptionPlanRepository.save(plan);
                            plansCreated++;
                        } else {
                            applyPlanFromJson(plan, planNode);
                            subscriptionPlanRepository.save(plan);
                            plansUpdated++;
                        }
                        planCodeToId.put(code, plan.getId());

                        JsonNode prices = planNode.get("prices");
                        if (prices != null && prices.isArray()) {
                            for (JsonNode priceNode : prices) {
                                BillingPeriodEnum period = BillingPeriodEnum.valueOf(
                                    priceNode.get("billingPeriod").asText()
                                );
                                Optional<SubscriptionPlanPrice> existingPrice = subscriptionPlanPriceRepository
                                    .findBySubscriptionPlanIdAndBillingPeriod(plan.getId(), period);
                                if (existingPrice.isPresent()) {
                                    SubscriptionPlanPrice price = existingPrice.get();
                                    applyPriceFromJson(price, priceNode);
                                    subscriptionPlanPriceRepository.save(price);
                                    pricesUpdated++;
                                } else {
                                    SubscriptionPlanPrice price = SubscriptionPlanPrice.builder()
                                        .subscriptionPlanId(plan.getId())
                                        .billingPeriod(period)
                                        .amountCents(priceNode.has("amountCents")
                                            ? priceNode.get("amountCents").asLong() : null)
                                        .listAmountCents(priceNode.has("listAmountCents")
                                            && !priceNode.get("listAmountCents").isNull()
                                            ? priceNode.get("listAmountCents").asLong() : null)
                                        .currency(priceNode.has("currency")
                                            ? priceNode.get("currency").asText() : "USD")
                                        .appleStoreProductId(textOrNull(priceNode, "appleStoreProductId"))
                                        .googlePlayProductId(textOrNull(priceNode, "googlePlayProductId"))
                                        .catalogActive(true)
                                        .build();
                                    assignId(price, priceNode);
                                    IdGenerator.assignIfAbsent(price);
                                    subscriptionPlanPriceRepository.save(price);
                                    pricesCreated++;
                                }
                            }
                        }

                        JsonNode features = planNode.get("features");
                        if (features != null && features.isArray()) {
                            for (JsonNode featureNode : features) {
                                String featureCode = featureNode.get("featureCode").asText();
                                Optional<SubscriptionPlanFeature> existingFeature =
                                    subscriptionPlanFeatureRepository
                                        .findBySubscriptionPlanIdOrderBySortOrderAscTitleAsc(plan.getId())
                                        .stream()
                                        .filter(f -> featureCode.equals(f.getFeatureCode()))
                                        .findFirst();
                                if (existingFeature.isPresent()) {
                                    SubscriptionPlanFeature feature = existingFeature.get();
                                    feature.setTitle(featureNode.get("title").asText());
                                    feature.setDescription(textOrNull(featureNode, "description"));
                                    if (featureNode.has("sortOrder")) {
                                        feature.setSortOrder(featureNode.get("sortOrder").asInt());
                                    }
                                    if (featureNode.has("included")) {
                                        feature.setIncluded(featureNode.get("included").asBoolean());
                                    }
                                    subscriptionPlanFeatureRepository.save(feature);
                                    featuresUpdated++;
                                } else {
                                    SubscriptionPlanFeature feature = SubscriptionPlanFeature.builder()
                                        .subscriptionPlanId(plan.getId())
                                        .featureCode(featureCode)
                                        .title(featureNode.get("title").asText())
                                        .description(textOrNull(featureNode, "description"))
                                        .sortOrder(featureNode.has("sortOrder")
                                            ? featureNode.get("sortOrder").asInt() : 0)
                                        .included(!featureNode.has("included")
                                            || featureNode.get("included").asBoolean())
                                        .build();
                                    assignId(feature, featureNode);
                                    IdGenerator.assignIfAbsent(feature);
                                    subscriptionPlanFeatureRepository.save(feature);
                                    featuresCreated++;
                                }
                            }
                        }
                    }

                    for (Map.Entry<String, String> inherit : planCodeToInheritsCode.entrySet()) {
                        UUID planId = planCodeToId.get(inherit.getKey());
                        UUID parentId = planCodeToId.get(inherit.getValue());
                        if (planId == null || parentId == null) {
                            continue;
                        }
                        subscriptionPlanRepository.findById(planId).ifPresent(plan -> {
                            if (plan.getInheritsFromPlanId() == null) {
                                plan.setInheritsFromPlanId(parentId);
                                subscriptionPlanRepository.save(plan);
                            }
                        });
                    }
                }
            }

            JsonNode profileSubs = root.get("profileSubscriptions");
            if (profileSubs != null && profileSubs.isArray()) {
                for (JsonNode ps : profileSubs) {
                    String email = ps.get("userEmail").asText();
                    String appCode = ps.get("appCode").asText();
                    String planCode = ps.get("planCode").asText();

                    Optional<App> appOpt = appRepository.findByCode(appCode);
                    if (appOpt.isEmpty()) {
                        continue;
                    }
                    App app = appOpt.get();
                    Optional<User> userOpt = userRepository.findByEmailAndAppId(email, app.getId());
                    if (userOpt.isEmpty()) {
                        continue;
                    }
                    Optional<UserProfile> profileOpt = userProfileRepository.findByUserIdAndAppId(
                        userOpt.get().getId(), app.getId()
                    );
                    if (profileOpt.isEmpty()) {
                        continue;
                    }
                    UserProfile profile = profileOpt.get();

                    Optional<SubscriptionPlan> planOpt = subscriptionPlanRepository.findByAppIdAndCode(
                        app.getId(), planCode
                    );
                    if (planOpt.isEmpty()) {
                        continue;
                    }

                    UUID seedId = null;
                    if (ps.has("id")) {
                        try {
                            seedId = UUID.fromString(ps.get("id").asText());
                        } catch (Exception ignore) {
                            // use generated id
                        }
                    }
                    if (seedId != null && profileSubscriptionRepository.existsById(seedId)) {
                        continue;
                    }
                    boolean alreadyActive = profileSubscriptionRepository
                        .findFirstByProfileIdAndStatusInOrderByStartedAtDesc(
                            profile.getId(),
                            SubscriptionDataLoader.activeStatuses()
                        )
                        .isPresent();
                    if (alreadyActive) {
                        continue;
                    }

                    Instant now = Instant.now();
                    ProfileSubscription row = ProfileSubscription.builder()
                        .profileId(profile.getId())
                        .subscriptionPlanId(planOpt.get().getId())
                        .status(parseStatus(ps))
                        .startedAt(now)
                        .currentPeriodStart(now)
                        .autoRenew(false)
                        .build();
                    if (seedId != null) {
                        row.setId(seedId);
                    }
                    IdGenerator.assignIfAbsent(row);
                    profileSubscriptionRepository.save(row);
                    profileSubsCreated++;
                }
            }

            System.out.println("[SubscriptionDataLoader] Summary:");
            System.out.println("  Plans created: " + plansCreated + ", updated: " + plansUpdated);
            System.out.println("  Prices created: " + pricesCreated + ", updated: " + pricesUpdated);
            System.out.println("  Features created: " + featuresCreated + ", updated: " + featuresUpdated);
            System.out.println("  Profile subscriptions created: " + profileSubsCreated);
        }
    }

    static java.util.List<SubscriptionStatusEnum> activeStatuses() {
        return java.util.List.of(
            SubscriptionStatusEnum.TRIALING,
            SubscriptionStatusEnum.ACTIVE,
            SubscriptionStatusEnum.PAST_DUE
        );
    }

    private static SubscriptionPricingUnitEnum parsePricingUnit(JsonNode planNode) {
        if (!planNode.has("pricingUnit") || planNode.get("pricingUnit").isNull()) {
            return SubscriptionPricingUnitEnum.FLAT;
        }
        return SubscriptionPricingUnitEnum.valueOf(planNode.get("pricingUnit").asText());
    }

    private static SubscriptionStatusEnum parseStatus(JsonNode ps) {
        if (ps.has("status")) {
            try {
                return SubscriptionStatusEnum.valueOf(ps.get("status").asText());
            } catch (Exception ignore) {
                // fall through
            }
        }
        return SubscriptionStatusEnum.ACTIVE;
    }

    private static String textOrNull(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull() ? node.get(field).asText() : null;
    }

    private static void assignId(BaseEntity entity, JsonNode node) {
        if (node.has("id")) {
            try {
                entity.setId(UUID.fromString(node.get("id").asText()));
            } catch (Exception ignore) {
                // generated on save
            }
        }
    }

    /** Sync catalog metadata from JSON when the plan row already exists (idempotent upsert). */
    private static void applyPlanFromJson(SubscriptionPlan plan, JsonNode planNode) {
        plan.setName(planNode.get("name").asText());
        plan.setDescription(textOrNull(planNode, "description"));
        if (planNode.has("sortOrder")) {
            plan.setSortOrder(planNode.get("sortOrder").asInt());
        }
        if (planNode.has("isFree")) {
            plan.setIsFree(planNode.get("isFree").asBoolean());
        }
        plan.setPricingUnit(parsePricingUnit(planNode));
        if (planNode.has("maxSeats") && !planNode.get("maxSeats").isNull()) {
            plan.setMaxSeats(planNode.get("maxSeats").asInt());
        } else {
            plan.setMaxSeats(null);
        }
        if (planNode.has("trialDays")) {
            plan.setTrialDays(planNode.get("trialDays").asInt());
        }
        plan.setCatalogActive(true);
    }

    private static void applyPriceFromJson(SubscriptionPlanPrice price, JsonNode priceNode) {
        if (priceNode.has("amountCents")) {
            price.setAmountCents(priceNode.get("amountCents").asLong());
        }
        if (priceNode.has("listAmountCents") && !priceNode.get("listAmountCents").isNull()) {
            price.setListAmountCents(priceNode.get("listAmountCents").asLong());
        } else {
            price.setListAmountCents(null);
        }
        if (priceNode.has("currency")) {
            price.setCurrency(priceNode.get("currency").asText());
        }
        price.setAppleStoreProductId(textOrNull(priceNode, "appleStoreProductId"));
        price.setGooglePlayProductId(textOrNull(priceNode, "googlePlayProductId"));
        price.setCatalogActive(true);
    }
}
