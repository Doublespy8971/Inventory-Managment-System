package com.project.code.Controller;

import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
public class ProductController {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ServiceClass serviceClass;
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    @PostMapping
    public ResponseEntity<Map<String, String>> addProduct(@RequestBody Product product) {
        Map<String, String> response = new HashMap<>();
        try {
            if (!serviceClass.validateProduct(product)) {
                response.put("message", "Product with this name already exists");
                return ResponseEntity.badRequest().body(response);
            }
            productRepository.save(product);
            response.put("message", "Product added successfully");
            return ResponseEntity.ok(response);
        } catch (DataIntegrityViolationException e) {
            response.put("message", "Error: SKU already exists or invalid data");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("message", "Error adding product: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/product/{id}")
    public ResponseEntity<Map<String, Object>> getProductbyId(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            response.put("products", product);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Product not found");
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping
    public ResponseEntity<Map<String, String>> updateProduct(@RequestBody Product product) {
        Map<String, String> response = new HashMap<>();
        try {
            productRepository.save(product);
            response.put("message", "Product updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error updating product: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/category/{name}/{category}")
    public ResponseEntity<Map<String, Object>> filterbyCategoryProduct(
            @PathVariable String name,
            @PathVariable String category) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products;
        
        if ("null".equals(name) && "null".equals(category)) {
            products = productRepository.findAll();
        } else if ("null".equals(name)) {
            products = productRepository.findByCategory(category);
        } else if ("null".equals(category)) {
            products = productRepository.findProductBySubName(name);
        } else {
            products = productRepository.findProductBySubNameAndCategory(name, category);
        }
        
        response.put("products", products);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> listProduct() {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = productRepository.findAll();
        response.put("products", products);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/filter/{category}/{storeid}")
    public ResponseEntity<Map<String, Object>> getProductbyCategoryAndStoreId(
            @PathVariable String category,
            @PathVariable Long storeid) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = productRepository.findProductByCategory(category, storeid);
        response.put("product", products);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            if (!serviceClass.ValidateProductId(id)) {
                response.put("message", "Product not found");
                return ResponseEntity.notFound().build();
            }
            inventoryRepository.deleteByProductId(id);
            productRepository.deleteById(id);
            response.put("message", "Product deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error deleting product: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/searchProduct/{name}")
    public ResponseEntity<Map<String, Object>> searchProduct(@PathVariable String name) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = productRepository.findProductBySubName(name);
        response.put("products", products);
        return ResponseEntity.ok(response);
    }
}
