package com.ayanlab.core.service.kafka;

public interface IKafkaProducerService {
    void send(String topic, String key, Object payload);
}
