package com.ayanlab.core.service.kafka;

public interface IKafkaConsumerService {
    void handle(String topic, Object payload);
}
