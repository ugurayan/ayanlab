package com.ayanlabs.common.kafka;

public interface KafkaConsumerService {
    void handle(String topic, Object payload);
}

