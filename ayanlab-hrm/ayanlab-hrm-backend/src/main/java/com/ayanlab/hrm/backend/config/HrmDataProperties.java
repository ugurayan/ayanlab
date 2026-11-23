package com.ayanlab.hrm.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "staffiq.db")
public class HrmDataProperties {
    private boolean reloadMockDataOnStartup = true;
    private String mockDataResource = "classpath:db/staffiq-mock-data.json";
    private boolean reloadNavigationOnStartup = true;
    private String navigationSeedResource = "classpath:db/staffiq-navigation-seed.json";
}