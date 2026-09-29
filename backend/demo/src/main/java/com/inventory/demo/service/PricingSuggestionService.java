package com.inventory.demo.service;

import com.inventory.demo.engine.CommerceAdvisor;
import com.inventory.demo.model.Product;
import com.inventory.demo.model.PricingSuggestion;
import com.inventory.demo.repository.PricingSuggestionRepository;
import com.inventory.demo.repository.ProductRepository;
import com.inventory.demo.enums.SuggestionStatus;
import com.inventory.demo.enums.TriggerReason;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PricingSuggestionService {
    
    @Autowired
    private PricingSuggestionRepository pricingSuggestionRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CommerceAdvisor commerceAdvisor;
    
    public PricingSuggestion createPricingSuggestion(
            Long productId, 
            TriggerReason triggerReason) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            
            // Use the pluggable commerce engine instead of hardcoded algorithm
            PricingSuggestion suggestion = commerceAdvisor.generatePricingSuggestion(product, triggerReason);
            
            return pricingSuggestionRepository.save(suggestion);
        }
        throw new RuntimeException("Product not found with id: " + productId);
    }
    public boolean hasPendingSuggestionsForProduct(Long productId, TriggerReason triggerReason) {
        try {
            List<PricingSuggestion> pricingSuggestions = pricingSuggestionRepository
                .findByPendingStatusAndProductId(productId);
            
            List<ReorderSuggestion> reorderSuggestions = reorderSuggestionRepository
                .findByPendingStatusAndProductId(productId);
            
            // Check if any of the suggestions match the trigger reason
            boolean hasPricingPending = pricingSuggestions.stream()
                .anyMatch(ps -> ps.getTriggerReason() == triggerReason);
                
            boolean hasReorderPending = reorderSuggestions.stream()
                .anyMatch(rs -> rs.getTriggerReason() == triggerReason);
                
            return hasPricingPending || hasReorderPending;
        } catch (Exception e) {
            // Log error but don't prevent processing
            return false;
        }
    }
    
    public Optional<PricingSuggestion> getPricingSuggestionById(Long id) {
        return pricingSuggestionRepository.findById(id);
    }
    
    public List<PricingSuggestion> getAllPricingSuggestions() {
        return pricingSuggestionRepository.findAll();
    }
    
    public List<PricingSuggestion> getPricingSuggestionsByStatus(SuggestionStatus status) {
        return pricingSuggestionRepository.findByStatus(status);
    }
    
    public PricingSuggestion updatePricingSuggestionStatus(Long suggestionId, SuggestionStatus status) {
        Optional<PricingSuggestion> optionalSuggestion = pricingSuggestionRepository.findById(suggestionId);
        if (optionalSuggestion.isPresent()) {
            PricingSuggestion suggestion = optionalSuggestion.get();
            suggestion.setStatus(status);
            
            // If accepted, update the product's current price
            if (status == SuggestionStatus.ACCEPTED) {
                Product product = suggestion.getProduct();
                product.setCurrentPrice(suggestion.getRecommendedPrice());
                productRepository.save(product);
            }
            
            return pricingSuggestionRepository.save(suggestion);
        }
        throw new RuntimeException("Pricing suggestion not found with id: " + suggestionId);
    }
}