package com.inventory.demo.service;

import com.inventory.demo.engine.CommerceAdvisor;
import com.inventory.demo.model.Product;
import com.inventory.demo.model.ReorderSuggestion;
import com.inventory.demo.repository.ReorderSuggestionRepository;
import com.inventory.demo.repository.ProductRepository;
import com.inventory.demo.enums.SuggestionStatus;
import com.inventory.demo.enums.TriggerReason;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReorderSuggestionService {
    
    @Autowired
    private ReorderSuggestionRepository reorderSuggestionRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CommerceAdvisor commerceAdvisor;
    
    public ReorderSuggestion createReorderSuggestion(
            Long productId, 
            TriggerReason triggerReason) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            
            // Use the pluggable commerce engine instead of hardcoded algorithm
            ReorderSuggestion suggestion = commerceAdvisor.generateReorderSuggestion(product, triggerReason);
            
            return reorderSuggestionRepository.save(suggestion);
        }
        throw new RuntimeException("Product not found with id: " + productId);
    }
    public boolean hasPendingSuggestionsForProduct(Long productId, TriggerReason triggerReason) {
        try {
            List<ReorderSuggestion> reorderSuggestions = reorderSuggestionRepository
                .findByPendingStatusAndProductId(productId);
            
            List<PricingSuggestion> pricingSuggestions = pricingSuggestionRepository
                .findByPendingStatusAndProductId(productId);
            
            // Check if any of the suggestions match the trigger reason
            boolean hasReorderPending = reorderSuggestions.stream()
                .anyMatch(rs -> rs.getTriggerReason() == triggerReason);
                
            boolean hasPricingPending = pricingSuggestions.stream()
                .anyMatch(ps -> ps.getTriggerReason() == triggerReason);
                
            return hasReorderPending || hasPricingPending;
        } catch (Exception e) {
            // Log error but don't prevent processing
            return false;
        }
    }
    
    public Optional<ReorderSuggestion> getReorderSuggestionById(Long id) {
        return reorderSuggestionRepository.findById(id);
    }
    
    public List<ReorderSuggestion> getAllReorderSuggestions() {
        return reorderSuggestionRepository.findAll();
    }
    
    public List<ReorderSuggestion> getReorderSuggestionsByStatus(SuggestionStatus status) {
        return reorderSuggestionRepository.findByStatus(status);
    }
    
    public ReorderSuggestion updateReorderSuggestionStatus(Long suggestionId, SuggestionStatus status) {
        Optional<ReorderSuggestion> optionalSuggestion = reorderSuggestionRepository.findById(suggestionId);
        if (optionalSuggestion.isPresent()) {
            ReorderSuggestion suggestion = optionalSuggestion.get();
            suggestion.setStatus(status);
            
            // If accepted, update the product's stock level (simulate inbound shipment)
            if (status == SuggestionStatus.ACCEPTED) {
                Product product = suggestion.getProduct();
                int newStockLevel = product.getStockLevel() + suggestion.getRecommendedQuantity();
                product.setStockLevel(newStockLevel);
                
                // Update lifecycle if needed
                if (newStockLevel > 0 && product.getLifecycle() == com.inventory.demo.enums.ProductLifecycle.OUT_OF_STOCK) {
                    product.setLifecycle(com.inventory.demo.enums.ProductLifecycle.ACTIVE);
                }
                
                productRepository.save(product);
            }
            
            return reorderSuggestionRepository.save(suggestion);
        }
        throw new RuntimeException("Reorder suggestion not found with id: " + suggestionId);
    }
}