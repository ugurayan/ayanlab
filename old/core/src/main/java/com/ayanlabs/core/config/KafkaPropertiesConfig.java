package com.ayanlabs.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "ayanlabs.kafka")
public class KafkaPropertiesConfig {
    private String bootstrapServers = "localhost:9092";
    private Consumer consumer = new Consumer();

    @Getter
    @Setter
    public static class Consumer {
        private String groupId = "ayanlabs-group";
    }
}
