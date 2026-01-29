package com.project.code.Controller;

import com.project.code.Model.CombinedRequest;
import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    @Autowired
    private ServiceClass serviceClass;
    
    @PutMapping
    public ResponseEntity<Map<String, String>> updateInventory(@RequestBody CombinedRequest combinedRequest) {
        Map<String, String> response = new HashMap<>();
        try {
            Product product = combinedRequest.getProduct();
            Inventory inventory = combinedRequest.getInventory();
            
            if (!serviceClass.ValidateProductId(product.getId())) {
                response.put("message", "Product not found");
                return ResponseEntity.badRequest().body(response);
            }
            
            Inventory existingInventory = serviceClass.getInventoryId(inventory);
            if (existingInventory != null) {
                existingInventory.setStockLevel(inventory.getStockLevel());
                productRepository.save(product);
                inventoryRepository.save(existingInventory);
                response.put("message", "Inventory updated successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "No inventory data available");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("message", "Error updating inventory: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping
    public ResponseEntity<Map<String, String>> saveInventory(@RequestBody Inventory inventory) {
        Map<String, String> response = new HashMap<>();
        try {
            if (!serviceClass.validateInventory(inventory)) {
                response.put("message", "Inventory already exists for this product and store");
                return ResponseEntity.badRequest().body(response);
            }
            inventoryRepository.save(inventory);
            response.put("message", "Inventory saved successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error saving inventory: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/{storeId}")
    public ResponseEntity<Map<String, Object>> getAllProducts(@PathVariable Long storeId) {
        Map<String, Object> response = new HashMap<>();
        List<Inventory> inventories = inventoryRepository.findByStore_Id(storeId);
        List<Product> products = inventories.stream()
            .map(Inventory::getProduct)
            .collect(Collectors.toList());
        
        // Add inventory info to each product
        for (Product product : products) {
            Inventory inv = inventories.stream()
                .filter(i -> i.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);
            if (inv != null) {
                product.setInventory(List.of(inv));
            }
        }
        
        response.put("products", products);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/filter/{category}/{name}/{storeId}")
    public ResponseEntity<Map<String, Object>> getProductName(
            @PathVariable String category,
            @PathVariable String name,
            @PathVariable Long storeId) {
        Map<String, Object> response = new HashMap<>();
        List<Inventory> inventories = inventoryRepository.findByStore_Id(storeId);
        List<Product> products = inventories.stream()
            .map(Inventory::getProduct)
            .filter(p -> {
                boolean categoryMatch = "null".equals(category) || p.getCategory().equals(category);
                boolean nameMatch = "null".equals(name) || p.getName().toLowerCase().contains(name.toLowerCase());
                return categoryMatch && nameMatch;
            })
            .collect(Collectors.toList());
        
        // Add inventory info
        for (Product product : products) {
            Inventory inv = inventories.stream()
                .filter(i -> i.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);
            if (inv != null) {
                product.setInventory(List.of(inv));
            }
        }
        
        response.put("product", products);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search/{name}/{storeId}")
    public ResponseEntity<Map<String, Object>> searchProduct(
            @PathVariable String name,
            @PathVariable Long storeId) {
        Map<String, Object> response = new HashMap<>();
        List<Inventory> inventories = inventoryRepository.findByStore_Id(storeId);
        List<Product> products = inventories.stream()
            .map(Inventory::getProduct)
            .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
            .collect(Collectors.toList());
        
        response.put("product", products);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> removeProduct(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            if (!serviceClass.ValidateProductId(id)) {
                response.put("message", "Product not found");
                return ResponseEntity.badRequest().body(response);
            }
            inventoryRepository.deleteByProductId(id);
            productRepository.deleteById(id);
            response.put("message", "Product removed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error removing product: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/validate/{quantity}/{storeId}/{productId}")
    public ResponseEntity<Boolean> validateQuantity(
            @PathVariable Integer quantity,
            @PathVariable Long storeId,
            @PathVariable Long productId) {
        Inventory inventory = inventoryRepository.findByProductIdandStoreId(productId, storeId);
        if (inventory != null && inventory.getStockLevel() >= quantity) {
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.ok(false);
    }
}
