package com.inventory.demo.repository;

import com.inventory.demo.model.Product;
import com.inventory.demo.enums.ProductLifecycle;
import com.inventory.demo.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);
    
    List<Product> findByLifecycle(ProductLifecycle lifecycle);
    
    List<Product> findByCategory(Category category);
    
    @Query("SELECT p FROM Product p WHERE " +
           "(:lifecycle IS NULL OR p.lifecycle = :lifecycle) AND " +
           "(:category IS NULL OR p.category = :category)")
    List<Product> findByLifecycleAndCategory(
        @Param("lifecycle") ProductLifecycle lifecycle,
        @Param("category") Category category
    );
    
    List<Product> findByStockLevelLessThanEqual(Integer threshold);
}