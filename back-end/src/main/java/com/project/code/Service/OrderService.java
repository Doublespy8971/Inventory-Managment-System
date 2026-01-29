package com.project.code.Service;

import com.project.code.Model.*;
import com.project.code.Repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class OrderService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private StoreRepository storeRepository;
    
    @Autowired
    private OrderDetailsRepository orderDetailsRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Transactional
    public void saveOrder(PlaceOrderRequestDTO placeOrderRequest) {
        // Retrieve or Create the Customer
        Customer customer = customerRepository.findByEmail(placeOrderRequest.getCustomerEmail());
        if (customer == null) {
            customer = new Customer(
                placeOrderRequest.getCustomerName(),
                placeOrderRequest.getCustomerEmail(),
                placeOrderRequest.getCustomerPhone()
            );
            customer = customerRepository.save(customer);
        }
        
        // Retrieve the Store
        Store store = storeRepository.findById(placeOrderRequest.getStoreId())
            .orElseThrow(() -> new RuntimeException("Store not found with ID: " + placeOrderRequest.getStoreId()));
        
        // Parse datetime
        LocalDateTime orderDate;
        if (placeOrderRequest.getDatetime() != null && !placeOrderRequest.getDatetime().isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            orderDate = LocalDateTime.parse(placeOrderRequest.getDatetime(), formatter);
        } else {
            orderDate = LocalDateTime.now();
        }
        
        // Create OrderDetails
        OrderDetails orderDetails = new OrderDetails(
            customer,
            store,
            placeOrderRequest.getTotalPrice(),
            orderDate
        );
        orderDetails = orderDetailsRepository.save(orderDetails);
        
        // Create and Save OrderItems
        List<PurchaseProductDTO> purchaseProducts = placeOrderRequest.getPurchaseProduct();
        for (PurchaseProductDTO purchaseProduct : purchaseProducts) {
            Product product = productRepository.findById(purchaseProduct.getId())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + purchaseProduct.getId()));
            
            // Update inventory
            Inventory inventory = inventoryRepository.findByProductIdandStoreId(
                product.getId(),
                store.getId()
            );
            if (inventory != null) {
                int newStockLevel = inventory.getStockLevel() - purchaseProduct.getQuantity();
                if (newStockLevel < 0) {
                    throw new RuntimeException("Insufficient stock for product: " + product.getName());
                }
                inventory.setStockLevel(newStockLevel);
                inventoryRepository.save(inventory);
            }
            
            // Create OrderItem
            OrderItem orderItem = new OrderItem(
                orderDetails,
                product,
                purchaseProduct.getQuantity(),
                purchaseProduct.getPrice()
            );
            orderItemRepository.save(orderItem);
        }
    }
}
