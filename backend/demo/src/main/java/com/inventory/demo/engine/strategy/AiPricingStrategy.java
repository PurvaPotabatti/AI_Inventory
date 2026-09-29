package com.inventory.demo.engine.strategy;

import com.inventory.demo.ai.LLMUtil;
import com.inventory.demo.engine.PricingStrategy;
import com.inventory.demo.model.Product;
import com.inventory.demo.model.PricingSuggestion;
import com.inventory.demo.enums.ChangeDirection;
import com.inventory.demo.enums.SuggestionStatus;
import com.inventory.demo.enums.TriggerReason;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.io.IOException;

public class AiPricingStrategy implements PricingStrategy {
    
    @Autowired
    private LLMUtil llmUtil;
    
    @Override
    public PricingSuggestion generatePricingSuggestion(Product product, TriggerReason triggerReason) {
        try {
            // Construct prompt with structured context as required
            String prompt = constructPricingPrompt(product, triggerReason);
            
            // Call LLM with timeout handling
            String jsonResponse = llmUtil.callLLM(prompt);
            
            // Parse and validate response
            JsonNode responseNode = llmUtil.parseAndValidateResponse(jsonResponse);
            
            // Extract values from response
            BigDecimal recommendedPrice = new BigDecimal(responseNode.get("recommended_price").asText());
            ChangeDirection changeDirection = ChangeDirection.valueOf(responseNode.get("change_direction").asText());
            double confidence = responseNode.get("confidence").asDouble();
            String reasoning = responseNode.get("reasoning").asText();
            
            // Validate recommended price is within sane bounds
            if (!isValidPriceRange(product.getCurrentPrice(), recommendedPrice)) {
                // Fallback to rule-based strategy if price is out of bounds
                return fallbackToRuleBasedPricing(product, triggerReason);
            }
            
            // Create and populate suggestion
            PricingSuggestion suggestion = new PricingSuggestion();
            suggestion.setProduct(product);
            suggestion.setCurrentPrice(product.getCurrentPrice());
            suggestion.setRecommendedPrice(recommendedPrice);
            suggestion.setChangeDirection(changeDirection);
            suggestion.setConfidence(confidence);
            suggestion.setReasoning(reasoning);
            suggestion.setStatus(SuggestionStatus.PENDING);
            suggestion.setTriggerReason(triggerReason);
            
            return suggestion;
            
        } catch (IOException e) {
            // Handle IO exceptions (timeouts, network errors, etc.) by falling back to rule-based strategy
            return fallbackToRuleBasedPricing(product, triggerReason);
        } catch (Exception e) {
            // Handle any other exceptions by falling back to rule-based strategy
            return fallbackToRuleBasedPricing(product, triggerReason);
        }
    }
    private String constructPricingPrompt(Product product, TriggerReason triggerReason) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a pricing recommendation for the following product:\n");
        prompt.append("Product: ").append(product.getName()).append("\n");
        prompt.append("Category: ").append(product.getCategory()).append("\n");
        prompt.append("Current Price: $").append(product.getCurrentPrice()).append("\n");
        prompt.append("Stock Level: ").append(product.getStockLevel()).append("\n");
        prompt.append("Reorder Threshold: ").append(product.getReorderThreshold()).append("\n");
        prompt.append("Demand Velocity: ").append(product.getDemandVelocity()).append(" units\n");
        prompt.append("Category Average Demand: 8 units (based on historical data)\n");
        prompt.append("Trigger Context: ").append(getTriggerDescription(triggerReason)).append("\n\n");
        
        prompt.append("Please provide a JSON response with the following fields:\n");
        prompt.append("- recommended_price: A positive decimal value for the suggested price\n");
        prompt.append("- change_direction: One of INCREASE, DECREASE, or HOLD\n");
        prompt.append("- confidence: A decimal value between 0.0 and 1.0 indicating confidence level\n");
        prompt.append("- reasoning: A detailed explanation of why this recommendation was made\n\n");
        
        prompt.append("Important considerations:\n");
        prompt.append("- Inventory-low situations may warrant price increases to protect scarce inventory or clearance discounts to move units quickly\n");
        prompt.append("- Demand-spike situations may warrant modest price increases to capitalize on popularity\n");
        prompt.append("- Ensure the recommended price is positive and within reasonable bounds (not more than 10x current price without strong justification)\n");
        
        return prompt.toString();
    }
    
    private String getTriggerDescription(TriggerReason triggerReason) {
        switch (triggerReason) {
            case INVENTORY_LOW:
                return "Inventory is critically low - immediate action needed";
            case DEMAND_SPIKE:
                return "Unusually high demand velocity detected - trending product";
            case MANUAL:
                return "Manual pricing request - merchant initiated review";
            case INITIAL:
                return "Initial product setup - establishing baseline pricing";
            default:
                return "Standard periodic review";
        }
    }
    
    private boolean isValidPriceRange(BigDecimal currentPrice, BigDecimal recommendedPrice) {
        // Ensure recommended price is positive
        if (recommendedPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        
        // Ensure recommended price is not more than 10x current price without strong justification
        BigDecimal maxPrice = currentPrice.multiply(new BigDecimal("10"));
        if (recommendedPrice.compareTo(maxPrice) > 0) {
            return false;
        }
        
        // Ensure recommended price is not less than 10% of current price (avoid extreme discounts)
        BigDecimal minPrice = currentPrice.multiply(new BigDecimal("0.1"));
        if (recommendedPrice.compareTo(minPrice) < 0) {
            return false;
        }
        
        return true;
    }
    
    private PricingSuggestion fallbackToRuleBasedPricing(Product product, TriggerReason triggerReason) {
        // Use the rule-based strategy as fallback
        RuleBasedPricingStrategy fallbackStrategy = new RuleBasedPricingStrategy();
        return fallbackStrategy.generatePricingSuggestion(product, triggerReason);
    }
}