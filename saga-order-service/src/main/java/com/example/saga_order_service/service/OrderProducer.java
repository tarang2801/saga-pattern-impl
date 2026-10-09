package com.example.saga_order_service.service;

import com.example.saga_order_service.entity.Order;
import com.example.saga_order_service.util.Constants;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {

    KafkaTemplate<String, Object> kafkaTemplate;
    OrderProducer(KafkaTemplate kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderEvent(Order order){
        System.out.println("Sending Order event:"+order.getId());
        kafkaTemplate.send(Constants.ORDER_CREATED_TOPIC, order);
    }
}
