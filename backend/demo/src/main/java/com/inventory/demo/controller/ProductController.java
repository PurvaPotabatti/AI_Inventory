package com.inventory.demo.controller;

import com.inventory.demo.model.Product;
import com.inventory.demo.service.ProductService;
import com.inventory.demo.service.PricingSuggestionService;
import com.inventory.demo.service.ReorderSuggestionService;
import com.inventory.demo.enums.ProductLifecycle;
import com.inventory.demo.enums.Category;
import com.inventory.demo.enums.TriggerReason;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private PricingSuggestionService pricingSuggestionService;
    
    @Autowired
    private ReorderSuggestionService reorderSuggestionService;
    
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.ok(createdProduct);
    }
    
    @GetMapping
    public ResponseEntity<List<Product>> getProducts(
            @RequestParam(required = false) ProductLifecycle lifecycle,
            @RequestParam(required = false) Category category) {
        List<Product> products = productService.getProductsByFilters(lifecycle, category);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Product> updateStockLevel(
            @PathVariable Long id,
            @RequestBody StockUpdateRequest request) {
        try {
            Product updatedProduct = productService.updateStockLevel(id, request.getStockLevel());
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/orders")
    public ResponseEntity<Product> simulateSale(@PathVariable Long id) {
        try {
            Product updatedProduct = productService.incrementDemandVelocity(id);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/suggest-pricing")
    public ResponseEntity<?> suggestPricing(@PathVariable Long id) {
        try {
            var suggestion = pricingSuggestionService.createPricingSuggestion(id, TriggerReason.MANUAL);
            return ResponseEntity.ok(suggestion);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/suggest-reorder")
    public ResponseEntity<?> suggestReorder(@PathVariable Long id) {
        try {
            var suggestion = reorderSuggestionService.createReorderSuggestion(id, TriggerReason.MANUAL);
            return ResponseEntity.ok(suggestion);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Helper class for stock update requests
    public static class StockUpdateRequest {
        private Integer stockLevel;
        
        public Integer getStockLevel() {
            return stockLevel;
        }
        
        public void setStockLevel(Integer stockLevel) {
            this.stockLevel = stockLevel;
        }
    }
}