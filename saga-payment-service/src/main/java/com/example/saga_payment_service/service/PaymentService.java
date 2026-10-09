package com.example.saga_payment_service.service;

import com.example.saga_payment_service.dto.Order;
import com.example.saga_payment_service.entity.Payment;
import com.example.saga_payment_service.repository.PaymentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class PaymentService {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private PaymentRepo repo;

    @KafkaListener(topics = "inventory-reserved")
    public void processPayment(Order order){
        Payment payment = new Payment();
        payment.setOrderId(order.getId());

        if (new Random().nextBoolean()){
            payment.setStatus("SUCCESS");
            repo.save(payment);
            kafkaTemplate.send("payment-processed", order);
        } else {
            payment.setStatus("FAILED");
            repo.save(payment);
//            inventory and order service will listen
            kafkaTemplate.send("payment-failed", order);

        }

    }
}
