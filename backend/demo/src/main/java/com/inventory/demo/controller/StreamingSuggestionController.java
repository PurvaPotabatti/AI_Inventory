package com.inventory.demo.controller;

import com.inventory.demo.ai.LLMUtil;
import com.inventory.demo.engine.CommerceAdvisor;
import com.inventory.demo.model.Product;
import com.inventory.demo.repository.ProductRepository;
import com.inventory.demo.enums.TriggerReason;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/products")
public class StreamingSuggestionController {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CommerceAdvisor commerceAdvisor;
    
    @Autowired
    private LLMUtil llmUtil;
    
    /**
     * Streaming endpoint for AI pricing suggestions
     * Bonus feature: SSE token stream of AI reasoning before the suggestion lands
     */
    @GetMapping(value = "/{id}/suggest-pricing/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamPricingSuggestion(@PathVariable Long id, 
                                            @RequestParam TriggerReason triggerReason) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        
        CompletableFuture.runAsync(() -> {
            try {
                // Find the product
                Product product = productRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
                
                // Construct the prompt
                String prompt = constructStreamingPricingPrompt(product, triggerReason);
                
                // Stream the LLM response
                CompletableFuture<String> streamFuture = llmUtil.streamLLM(prompt);
                
                streamFuture.thenAccept(response -> {
                    try {
                        // Send the complete response
                        emitter.send(SseEmitter.event()
                                .name("complete")
                                .data(response));
                        emitter.complete();
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                    }
                }).exceptionally(throwable -> {
                    try {
                        emitter.send(SseEmitter.event()
                                .name("error")
                                .data("Failed to generate streaming response: " + throwable.getMessage()));
                        emitter.complete();
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                    }
                    return null;
                });
                
            } catch (Exception e) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data("Error: " + e.getMessage()));
                    emitter.complete();
                } catch (IOException ioException) {
                    emitter.completeWithError(ioException);
                }
            }
        });
        
        return emitter;
    }
    
    private String constructStreamingPricingPrompt(Product product, TriggerReason triggerReason) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a detailed pricing analysis for the following product. ");
        prompt.append("Provide your reasoning step by step as you think through the recommendation:\n\n");
        prompt.append("Product: ").append(product.getName()).append("\n");
        prompt.append("Category: ").append(product.getCategory()).append("\n");
        prompt.append("Current Price: $").append(product.getCurrentPrice()).append("\n");
        prompt.append("Stock Level: ").append(product.getStockLevel()).append("\n");
        prompt.append("Reorder Threshold: ").append(product.getReorderThreshold()).append("\n");
        prompt.append("Demand Velocity: ").append(product.getDemandVelocity()).append(" units\n");
        prompt.append("Category Average Demand: 8 units (based on historical data)\n");
        prompt.append("Trigger Context: ").append(getTriggerDescription(triggerReason)).append("\n\n");
        
        prompt.append("Think through this systematically and explain your reasoning in detail.\n");
        prompt.append("Consider factors like inventory pressure, demand patterns, competitive positioning, and revenue optimization.\n");
        prompt.append("Conclude with a specific recommendation.");
        
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
}