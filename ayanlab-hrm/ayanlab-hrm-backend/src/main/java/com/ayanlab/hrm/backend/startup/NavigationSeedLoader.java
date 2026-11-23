package com.ayanlab.hrm.backend.startup;

import com.ayanlab.hrm.backend.config.HrmDataProperties;
import com.ayanlab.hrm.backend.service.NavigationSeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** Loads navigation menu items, labels, and supported locales from JSON. */
@Component
@Order(40)
@RequiredArgsConstructor
public class NavigationSeedLoader implements ApplicationRunner {

    private final HrmDataProperties dataProperties;
    private final NavigationSeedService navigationSeedService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!dataProperties.isReloadNavigationOnStartup()) {
            System.out.println(
                    "[NavigationSeedLoader] Disabled (staffiq.db.reload-navigation-on-startup=false)");
            return;
        }
        navigationSeedService.reloadFromClasspathResource();
        System.out.println(
                "[NavigationSeedLoader] Reloaded navigation seed from "
                        + dataProperties.getNavigationSeedResource());
    }
}