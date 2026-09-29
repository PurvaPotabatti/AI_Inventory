package com.inventory.demo.engine.strategy;

import com.inventory.demo.engine.ReorderStrategy;
import com.inventory.demo.model.Product;
import com.inventory.demo.model.ReorderSuggestion;
import com.inventory.demo.enums.SuggestionStatus;
import com.inventory.demo.enums.TriggerReason;

public class RuleBasedReorderStrategy implements ReorderStrategy {
    
    @Override
    public ReorderSuggestion generateReorderSuggestion(Product product, TriggerReason triggerReason) {
        int currentStock = product.getStockLevel();
        int recommendedQuantity = calculateRecommendedQuantity(product, triggerReason);
        int leadTimeDays = calculateLeadTimeDays(product, triggerReason);
        double confidence = calculateConfidence(product, triggerReason);
        String reasoning = generateReasoning(product, recommendedQuantity, leadTimeDays, triggerReason);
        
        ReorderSuggestion suggestion = new ReorderSuggestion();
        suggestion.setProduct(product);
        suggestion.setCurrentStock(currentStock);
        suggestion.setRecommendedQuantity(recommendedQuantity);
        suggestion.setSuggestedLeadTimeDays(leadTimeDays);
        suggestion.setConfidence(confidence);
        suggestion.setReasoning(reasoning);
        suggestion.setStatus(SuggestionStatus.PENDING);
        suggestion.setTriggerReason(triggerReason);
        
        return suggestion;
    }
    
    private int calculateRecommendedQuantity(Product product, TriggerReason triggerReason) {
        int currentStock = product.getStockLevel();
        int reorderThreshold = product.getReorderThreshold();
        
        // Rule-based reorder logic as per requirements:
        // recommend quantity = (reorder threshold × 3) − current stock, minimum 1
        
        int recommendedQuantity = (reorderThreshold * 3) - currentStock;
        return Math.max(1, recommendedQuantity); // Ensure minimum of 1 unit
    }
    
    private int calculateLeadTimeDays(Product product, TriggerReason triggerReason) {
        // Simple lead time calculation based on trigger reason
        switch (triggerReason) {
            case INVENTORY_LOW:
                return 2; // Expedited shipping for low inventory
            case DEMAND_SPIKE:
                return 3; // Faster shipping for high demand
            case INITIAL:
                return 5; // Standard lead time for initial setup
            default:
                return 7; // Default lead time
        }
    }
    
    private double calculateConfidence(Product product, TriggerReason triggerReason) {
        // Simple confidence calculation based on trigger reason
        switch (triggerReason) {
            case INVENTORY_LOW:
                return 0.95; // Very high confidence for critical inventory issues
            case DEMAND_SPIKE:
                return 0.85; // High confidence for demand-driven reorders
            case INITIAL:
                return 0.75; // Medium-high confidence for initial setup
            default:
                return 0.7; // Standard confidence for other cases
        }
    }
    
    private String generateReasoning(Product product, int recommendedQuantity, 
                                   int leadTimeDays, TriggerReason triggerReason) {
        StringBuilder reasoning = new StringBuilder();
        
        if (triggerReason == TriggerReason.INVENTORY_LOW) {
            reasoning.append("Critical inventory level detected (")
                     .append(product.getStockLevel())
                     .append(" items remaining, reorder threshold is ")
                     .append(product.getReorderThreshold())
                     .append("). ");
        } else if (triggerReason == TriggerReason.DEMAND_SPIKE) {
            reasoning.append("Unexpectedly high demand velocity (")
                     .append(product.getDemandVelocity())
                     .append(" units sold recently). ");
        } else if (triggerReason == TriggerReason.INITIAL) {
            reasoning.append("Initial reorder quantity calculated for new product setup. ");
        } else {
            reasoning.append("Standard reorder threshold reached. ");
        }
        
        reasoning.append("Recommend ordering ")
                 .append(recommendedQuantity)
                 .append(" units to maintain adequate stock levels. ");
                 
        reasoning.append("Estimated lead time: ")
                 .append(leadTimeDays)
                 .append(" business days for delivery.");
        
        return reasoning.toString();
    }
}