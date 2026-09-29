package com.inventory.demo.service;

import com.inventory.demo.events.StockLevelChangedEvent;
import com.inventory.demo.events.DemandVelocityChangedEvent;
import com.inventory.demo.model.Product;
import com.inventory.demo.repository.ProductRepository;
import com.inventory.demo.enums.ProductLifecycle;
import com.inventory.demo.enums.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    public Product createProduct(Product product) {
        // Set initial lifecycle state
        if (product.getStockLevel() <= 0) {
            product.setLifecycle(ProductLifecycle.OUT_OF_STOCK);
        } else {
            product.setLifecycle(ProductLifecycle.ACTIVE);
        }
        return productRepository.save(product);
    }
    
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    public Optional<Product> getProductBySku(String sku) {
        return productRepository.findBySku(sku);
    }
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public List<Product> getProductsByFilters(ProductLifecycle lifecycle, Category category) {
        if (lifecycle == null && category == null) {
            return productRepository.findAll();
        } else if (lifecycle != null && category != null) {
            return productRepository.findByLifecycleAndCategory(lifecycle, category);
        } else if (lifecycle != null) {
            return productRepository.findByLifecycle(lifecycle);
        } else {
            return productRepository.findByCategory(category);
        }
    }
    
    public Product updateStockLevel(Long productId, Integer newStockLevel) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            Integer oldStockLevel = product.getStockLevel();
            product.setStockLevel(newStockLevel);
            
            // Update lifecycle based on stock level
            if (newStockLevel <= 0) {
                product.setLifecycle(ProductLifecycle.OUT_OF_STOCK);
            } else if (product.getLifecycle() == ProductLifecycle.OUT_OF_STOCK) {
                product.setLifecycle(ProductLifecycle.ACTIVE);
            }
            
            Product savedProduct = productRepository.save(product);
            
            // Publish event for stock level change
            eventPublisher.publishEvent(new StockLevelChangedEvent(this, savedProduct, oldStockLevel, newStockLevel));
            
            return savedProduct;
        }
        throw new RuntimeException("Product not found with id: " + productId);
    }
    
    public Product incrementDemandVelocity(Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            Integer oldDemandVelocity = product.getDemandVelocity();
            product.setDemandVelocity(product.getDemandVelocity() + 1);
            Product savedProduct = productRepository.save(product);
            
            // Publish event for demand velocity change
            // Using category average of 5 as a default - in a real system this would come from analytics
            eventPublisher.publishEvent(new DemandVelocityChangedEvent(this, savedProduct, oldDemandVelocity, savedProduct.getDemandVelocity(), 5));
            
            return savedProduct;
        }
        throw new RuntimeException("Product not found with id: " + productId);
    }
    
    public List<Product> getLowStockProducts() {
        return productRepository.findByStockLevelLessThanEqual(
            productRepository.findAll().stream()
                .mapToInt(Product::getReorderThreshold)
                .max()
                .orElse(0)
        );
    }
}