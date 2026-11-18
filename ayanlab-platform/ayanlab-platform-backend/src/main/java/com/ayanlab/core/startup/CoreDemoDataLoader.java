package com.ayanlab.core.startup;

import com.ayanlab.core.condition.CoreSeedSurface;
import com.ayanlab.core.entity.App;
import com.ayanlab.core.entity.BankAccount;
import com.ayanlab.core.entity.BankCard;
import com.ayanlab.core.entity.User;
import com.ayanlab.core.entity.UserProfile;
import com.ayanlab.core.repository.AppRepository;
import com.ayanlab.core.repository.BankAccountRepository;
import com.ayanlab.core.repository.BankCardRepository;
import com.ayanlab.core.repository.UserProfileRepository;
import com.ayanlab.core.repository.UserRepository;
import com.ayanlab.core.util.IdGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Profile-scoped demo rows for core business entities (runs after auth seed on shared DB).
 */
@CoreSeedSurface
@Component
@Order(25)
public class CoreDemoDataLoader implements CommandLineRunner {

    private static final String CORE_APP = "core";
    private static final String CORE_ADMIN_EMAIL = "admin@core.local";

    @Value("${platform.db.load-demo-data:true}")
    private boolean loadDemoData;

    private final AppRepository appRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final BankAccountRepository bankAccountRepository;
    private final BankCardRepository bankCardRepository;

    public CoreDemoDataLoader(AppRepository appRepository,
                              UserRepository userRepository,
                              UserProfileRepository userProfileRepository,
                              BankAccountRepository bankAccountRepository,
                              BankCardRepository bankCardRepository) {
        this.appRepository = appRepository;
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.bankCardRepository = bankCardRepository;
    }

    @Override
    public void run(String... args) {
        if (!loadDemoData) {
            System.out.println("[CoreDemoDataLoader] Disabled");
            return;
        }
        Optional<App> coreApp = appRepository.findByCode(CORE_APP);
        if (coreApp.isEmpty()) {
            System.out.println("[CoreDemoDataLoader] Skipped: core app missing (start auth first)");
            return;
        }
        Optional<User> admin = userRepository.findByEmailAndAppId(CORE_ADMIN_EMAIL, coreApp.get().getId());
        if (admin.isEmpty()) {
            System.out.println("[CoreDemoDataLoader] Skipped: " + CORE_ADMIN_EMAIL + " not found");
            return;
        }
        List<UserProfile> profiles = userProfileRepository
            .findByUserIdAndAppIdAndIsActiveTrueOrderByCreatedAtDesc(admin.get().getId(), coreApp.get().getId());
        if (profiles.isEmpty()) {
            System.out.println("[CoreDemoDataLoader] Skipped: no profile for core admin");
            return;
        }
        UserProfile profile = profiles.getFirst();
        seedBankAccount(coreApp.get().getId(), profile.getId());
        seedBankCard(coreApp.get().getId(), profile.getId());
        System.out.println("[CoreDemoDataLoader] Demo bank account and card ensured for profile " + profile.getId());
    }

    private void seedBankAccount(UUID appId, UUID profileId) {
        if (!bankAccountRepository.findByProfileIdAndIsActiveTrueOrderByCreatedAtDesc(profileId).isEmpty()) {
            return;
        }
        BankAccount acct = BankAccount.builder()
            .appId(appId)
            .profileId(profileId)
            .bankName("Demo National Bank")
            .accountName("Operating Account")
            .accountNumberMasked("****4321")
            .routingNumberMasked("****0021")
            .currency("USD")
            .balanceCents(250_000L)
            .isDefault(true)
            .build();
        IdGenerator.assignIfAbsent(acct);
        bankAccountRepository.save(acct);
    }

    private void seedBankCard(UUID appId, UUID profileId) {
        if (!bankCardRepository.findByProfileIdAndIsActiveTrueOrderByCreatedAtDesc(profileId).isEmpty()) {
            return;
        }
        BankCard card = BankCard.builder()
            .appId(appId)
            .profileId(profileId)
            .cardholderName("Core Admin")
            .brand("VISA")
            .lastFour("4242")
            .expiryMonth(12)
            .expiryYear(2030)
            .tokenRef("demo_token_ref")
            .isDefault(true)
            .build();
        IdGenerator.assignIfAbsent(card);
        bankCardRepository.save(card);
    }
}