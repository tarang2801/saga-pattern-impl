package com.example.saga_order_service.service;

import com.example.saga_order_service.entity.Order;
import com.example.saga_order_service.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private OrderProducer producer;

    @Autowired
    private OrderRepository repo;

    public String createOrder(Order order){
        order.setStatus("PENDING");
        repo.save(order);
        producer.sendOrderEvent(order);
        return "Order created and saga started";

    }
}
