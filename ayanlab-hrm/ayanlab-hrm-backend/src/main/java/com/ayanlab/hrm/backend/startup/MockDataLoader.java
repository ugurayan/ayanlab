package com.ayanlab.hrm.backend.startup;

import com.ayanlab.hrm.backend.config.HrmDataProperties;
import com.ayanlab.hrm.backend.service.MockDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** Startup hook: delegates to {@link MockDataService}. */
@Component
@Order(50)
@RequiredArgsConstructor
public class MockDataLoader implements ApplicationRunner {

    private final HrmDataProperties dataProperties;
    private final MockDataService mockDataService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!dataProperties.isReloadMockDataOnStartup()) {
            System.out.println("[MockDataLoader] Disabled (staffiq.db.reload-mock-data-on-startup=false)");
            return;
        }
        mockDataService.reloadFromClasspathResource();
        System.out.println(
                "[MockDataLoader] Reloaded domain + CRUD seed from " + dataProperties.getMockDataResource());
    }
}