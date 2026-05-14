package com.ayanlabs.common.kafka;

public interface KafkaProducerService {
    void send(String topic, String key, Object payload);
}

