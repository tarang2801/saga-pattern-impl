package com.example.saga_inventory_service.service;

import com.example.saga_inventory_service.dto.Order;
import com.example.saga_inventory_service.entity.Inventory;
import com.example.saga_inventory_service.repository.InventoryRepository;
import com.example.saga_inventory_service.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @KafkaListener(topics = Constants.ORDER_CREATED_TOPIC, groupId = "saga-group")
    public void reserveInventory(Order order){
        Inventory inventory = inventoryRepository.findById(order.getId()).get();

        if (inventory.getQuantity() >= order.getQuantity()){
            inventory.setQuantity(inventory.getQuantity() - order.getQuantity());
            inventoryRepository.save(inventory);
            kafkaTemplate.send("inventory-reserved", order);
            System.out.println("Inventory reserved for order :"+order.getId());
        }else{
            System.out.println("Quantity not available for order:" + order.getId());
            kafkaTemplate.send("quantity-not-available", order);
        }
    }

    @KafkaListener(topics = Constants.PAYMENT_FAILED_TOPIC, groupId = "saga-group")
    public void releaseInventory(Order order){
        System.out.println("Releasing inventory for order: "+ order.getId());
        Inventory inventory = inventoryRepository.findById(order.getId()).get();
        inventory.setQuantity(order.getQuantity()+inventory.getQuantity());
        inventoryRepository.save(inventory);
        kafkaTemplate.send("inventory-released", order);
    }
}
