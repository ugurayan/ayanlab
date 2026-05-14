//package com.ayanlabs.common.kafka;
//
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//
//@Service
//public class SimpleKafkaConsumer implements KafkaConsumerService {
//
//    @KafkaListener(topics = "default-topic", groupId = "ayanlabs-group")
//    @Override
//    public void handle(String topic, Object payload) {
//        // kafka listener wiring would call specific handler methods; keeping simple.
//        System.out.println("Received payload on topic: " + topic + " payload=" + payload);
//    }
//}
//
