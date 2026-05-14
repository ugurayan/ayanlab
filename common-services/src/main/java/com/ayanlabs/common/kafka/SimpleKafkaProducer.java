//package com.ayanlabs.common.kafka;
//
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//@Service
//public class SimpleKafkaProducer implements KafkaProducerService {
//    private final KafkaTemplate<String, Object> kafkaTemplate;
//
//    public SimpleKafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
//        this.kafkaTemplate = kafkaTemplate;
//    }
//
//    @Override
//    public void send(String topic, String key, Object payload) {
//        kafkaTemplate.send(topic, key, payload);
//    }
//}
//
