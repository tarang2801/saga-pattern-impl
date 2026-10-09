package com.example.saga_order_service.controller;

import com.example.saga_order_service.entity.Order;
import com.example.saga_order_service.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/orders")
    public String createOrder(@RequestBody Order order){
        return orderService.createOrder(order);
    }


    
}
