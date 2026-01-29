package com.project.code.Repo;

import com.project.code.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findAll();
    
    List<Product> findByCategory(String category);
    
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
    
    Product findBySku(String sku);
    
    Product findByName(String name);
    
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:pname%")
    List<Product> findProductBySubName(@Param("pname") String pname);
    
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:pname% AND p.category = :category")
    List<Product> findProductBySubNameAndCategory(@Param("pname") String pname, @Param("category") String category);
    
    @Query("SELECT DISTINCT p FROM Product p JOIN p.inventory i WHERE i.store.id = :storeId AND p.category = :category")
    List<Product> findProductByCategory(@Param("category") String category, @Param("storeId") Long storeId);
}
