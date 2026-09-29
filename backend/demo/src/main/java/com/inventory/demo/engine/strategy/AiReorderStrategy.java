package com.inventory.demo.engine.strategy;

import com.inventory.demo.ai.LLMUtil;
import com.inventory.demo.engine.ReorderStrategy;
import com.inventory.demo.model.Product;
import com.inventory.demo.model.ReorderSuggestion;
import com.inventory.demo.enums.SuggestionStatus;
import com.inventory.demo.enums.TriggerReason;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class AiReorderStrategy implements ReorderStrategy {
    
    @Autowired
    private LLMUtil llmUtil;
    
    @Override
    public ReorderSuggestion generateReorderSuggestion(Product product, TriggerReason triggerReason) {
        try {
            // Construct prompt with structured context as required
            String prompt = constructReorderPrompt(product, triggerReason);
            
            // Call LLM with timeout handling
            String jsonResponse = llmUtil.callLLM(prompt);
            
            // Parse and validate response
            JsonNode responseNode = llmUtil.parseAndValidateResponse(jsonResponse);
            
            // Extract values from response
            int recommendedQuantity = responseNode.get("recommended_quantity").asInt();
            double confidence = responseNode.get("confidence").asDouble();
            String reasoning = responseNode.get("reasoning").asText();
            
            // Validate recommended quantity is positive
            if (recommendedQuantity <= 0) {
                // Fallback to rule-based strategy if quantity is invalid
                return fallbackToRuleBasedReorder(product, triggerReason);
            }
            
            // Estimate lead time (would typically come from LLM or supplier data)
            int leadTimeDays = estimateLeadTime(product, triggerReason);
            
            // Create and populate suggestion
            ReorderSuggestion suggestion = new ReorderSuggestion();
            suggestion.setProduct(product);
            suggestion.setCurrentStock(product.getStockLevel());
            suggestion.setRecommendedQuantity(recommendedQuantity);
            suggestion.setSuggestedLeadTimeDays(leadTimeDays);
            suggestion.setConfidence(confidence);
            suggestion.setReasoning(reasoning);
            suggestion.setStatus(SuggestionStatus.PENDING);
            suggestion.setTriggerReason(triggerReason);
            
            return suggestion;
            
        } catch (IOException e) {
            // Handle IO exceptions (timeouts, network errors, etc.) by falling back to rule-based strategy
            return fallbackToRuleBasedReorder(product, triggerReason);
        } catch (Exception e) {
            // Handle any other exceptions by falling back to rule-based strategy
            return fallbackToRuleBasedReorder(product, triggerReason);
        }
    }
    private String constructReorderPrompt(Product product, TriggerReason triggerReason) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a reorder recommendation for the following product:\n");
        prompt.append("Product: ").append(product.getName()).append("\n");
        prompt.append("Category: ").append(product.getCategory()).append("\n");
        prompt.append("Current Stock Level: ").append(product.getStockLevel()).append("\n");
        prompt.append("Reorder Threshold: ").append(product.getReorderThreshold()).append("\n");
        prompt.append("Demand Velocity: ").append(product.getDemandVelocity()).append(" units\n");
        prompt.append("Category Average Demand: 8 units (based on historical data)\n");
        prompt.append("Trigger Context: ").append(getTriggerDescription(triggerReason)).append("\n\n");
        
        prompt.append("Please provide a JSON response with the following fields:\n");
        prompt.append("- recommended_quantity: A positive integer for the suggested reorder quantity\n");
        prompt.append("- confidence: A decimal value between 0.0 and 1.0 indicating confidence level\n");
        prompt.append("- reasoning: A detailed explanation of why this recommendation was made\n\n");
        
        prompt.append("Important considerations:\n");
        prompt.append("- Inventory-low situations require urgent restocking to prevent stockouts\n");
        prompt.append("- Demand-spike situations require larger orders to meet sustained demand\n");
        prompt.append("- Ensure the recommended quantity is a positive integer\n");
        
        return prompt.toString();
    }
    
    private String getTriggerDescription(TriggerReason triggerReason) {
        switch (triggerReason) {
            case INVENTORY_LOW:
                return "Inventory is critically low - immediate restocking needed";
            case DEMAND_SPIKE:
                return "Unusually high demand velocity detected - trending product";
            case MANUAL:
                return "Manual reorder request - merchant initiated review";
            case INITIAL:
                return "Initial product setup - establishing baseline inventory";
            default:
                return "Standard periodic review";
        }
    }
    
    private int estimateLeadTime(Product product, TriggerReason triggerReason) {
        // In a real implementation, this might come from:
        // 1. LLM analysis of supplier data
        // 2. Historical supplier performance
        // 3. Current supply chain conditions
        
        // For now, use simple rules based on trigger context
        switch (triggerReason) {
            case INVENTORY_LOW:
                return 2; // Expedited shipping for critical situations
            case DEMAND_SPIKE:
                return 3; // Faster shipping for trending products
            case INITIAL:
                return 5; // Standard lead time for new products
            default:
                return 7; // Default lead time
        }
    }
    
    private ReorderSuggestion fallbackToRuleBasedReorder(Product product, TriggerReason triggerReason) {
        // Use the rule-based strategy as fallback
        RuleBasedReorderStrategy fallbackStrategy = new RuleBasedReorderStrategy();
        return fallbackStrategy.generateReorderSuggestion(product, triggerReason);
    }
}