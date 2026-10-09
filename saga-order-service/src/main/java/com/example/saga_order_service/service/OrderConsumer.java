package com.example.saga_order_service.service;

import com.example.saga_order_service.entity.Order;
import com.example.saga_order_service.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderConsumer {

    @Autowired
    OrderRepository repo;

    @KafkaListener(topics = "payment-processed", groupId = "saga-group")
    public void handlePaymentSuccess(Order order){
        Optional<Order> existingOrder = repo.findById(order.getId());
        if (existingOrder.isPresent()){
            existingOrder.get().setStatus("COMPLETE");
        }
    }

    @KafkaListener(topics = "payment-failure", groupId = "saga-group")
    public void handlePaymentFailure(Order order){
        Optional<Order> existingOrder = repo.findById(order.getId());
        if (existingOrder.isPresent()){
            existingOrder.get().setStatus("CANCELLED");
        }
    }

}
