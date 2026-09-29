package com.inventory.demo.engine.strategy;

import com.inventory.demo.engine.PricingStrategy;
import com.inventory.demo.model.Product;
import com.inventory.demo.model.PricingSuggestion;
import com.inventory.demo.enums.ChangeDirection;
import com.inventory.demo.enums.SuggestionStatus;
import com.inventory.demo.enums.TriggerReason;

import java.math.BigDecimal;

public class RuleBasedPricingStrategy implements PricingStrategy {
    
    @Override
    public PricingSuggestion generatePricingSuggestion(Product product, TriggerReason triggerReason) {
        BigDecimal currentPrice = product.getCurrentPrice();
        BigDecimal recommendedPrice = calculateRecommendedPrice(product, triggerReason);
        ChangeDirection changeDirection = determineChangeDirection(currentPrice, recommendedPrice);
        double confidence = calculateConfidence(product, triggerReason);
        String reasoning = generateReasoning(product, recommendedPrice, changeDirection, triggerReason);
        
        PricingSuggestion suggestion = new PricingSuggestion();
        suggestion.setProduct(product);
        suggestion.setCurrentPrice(currentPrice);
        suggestion.setRecommendedPrice(recommendedPrice);
        suggestion.setChangeDirection(changeDirection);
        suggestion.setConfidence(confidence);
        suggestion.setReasoning(reasoning);
        suggestion.setStatus(SuggestionStatus.PENDING);
        suggestion.setTriggerReason(triggerReason);
        
        return suggestion;
    }
    
    private BigDecimal calculateRecommendedPrice(Product product, TriggerReason triggerReason) {
        BigDecimal currentPrice = product.getCurrentPrice();
        int stockLevel = product.getStockLevel();
        int reorderThreshold = product.getReorderThreshold();
        int demandVelocity = product.getDemandVelocity();
        
        // Rule-based pricing logic as per requirements:
        // if stock < reorder threshold, recommend 10% price increase
        // if demand velocity > 2× category average, recommend 5% increase
        // otherwise HOLD
        
        // Simplified category average calculation for demonstration
        int categoryAverageDemand = 5; // This would normally come from analytics
        
        if (stockLevel < reorderThreshold) {
            // Stock below reorder threshold, recommend 10% price increase
            return currentPrice.multiply(BigDecimal.valueOf(1.10));
        } else if (demandVelocity > 2 * categoryAverageDemand) {
            // Demand velocity > 2× category average, recommend 5% increase
            return currentPrice.multiply(BigDecimal.valueOf(1.05));
        }
        
        // Default: hold current price
        return currentPrice;
    }
    
    private ChangeDirection determineChangeDirection(BigDecimal currentPrice, BigDecimal recommendedPrice) {
        int comparison = recommendedPrice.compareTo(currentPrice);
        if (comparison > 0) {
            return ChangeDirection.INCREASE;
        } else if (comparison < 0) {
            return ChangeDirection.DECREASE;
        } else {
            return ChangeDirection.HOLD;
        }
    }
    
    private double calculateConfidence(Product product, TriggerReason triggerReason) {
        // Simple confidence calculation based on trigger reason
        switch (triggerReason) {
            case INVENTORY_LOW:
                return 0.9; // High confidence for inventory triggers
            case DEMAND_SPIKE:
                return 0.8; // High confidence for demand triggers
            case INITIAL:
                return 0.7; // Medium confidence for initial suggestions
            default:
                return 0.6; // Lower confidence for manual requests
        }
    }
    
    private String generateReasoning(Product product, BigDecimal recommendedPrice, 
                                   ChangeDirection changeDirection, TriggerReason triggerReason) {
        StringBuilder reasoning = new StringBuilder();
        
        if (triggerReason == TriggerReason.INVENTORY_LOW) {
            reasoning.append("Low inventory detected (")
                     .append(product.getStockLevel())
                     .append(" items remaining, reorder threshold is ")
                     .append(product.getReorderThreshold())
                     .append("). ");
        } else if (triggerReason == TriggerReason.DEMAND_SPIKE) {
            reasoning.append("High demand velocity detected (")
                     .append(product.getDemandVelocity())
                     .append(" units sold recently). ");
        } else if (triggerReason == TriggerReason.INITIAL) {
            reasoning.append("Initial pricing suggestion for product setup. ");
        } else {
            reasoning.append("Standard pricing review triggered. ");
        }
        
        if (changeDirection == ChangeDirection.INCREASE) {
            reasoning.append("Recommend increasing price to $")
                     .append(recommendedPrice)
                     .append(" to optimize revenue while supply is limited.");
        } else if (changeDirection == ChangeDirection.DECREASE) {
            reasoning.append("Recommend decreasing price to $")
                     .append(recommendedPrice)
                     .append(" to stimulate demand and reduce excess inventory.");
        } else {
            reasoning.append("Recommend holding current price at $")
                     .append(recommendedPrice)
                     .append(" as market conditions appear stable.");
        }
        
        return reasoning.toString();
    }
}