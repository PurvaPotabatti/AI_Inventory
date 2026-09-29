package com.inventory.demo.events;

import com.inventory.demo.enums.TriggerReason;
import com.inventory.demo.model.PricingSuggestion;
import com.inventory.demo.model.ReorderSuggestion;
import com.inventory.demo.service.PricingSuggestionService;
import com.inventory.demo.service.ReorderSuggestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class RecommendationEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(RecommendationEventListener.class);
    
    @Autowired
    private PricingSuggestionService pricingSuggestionService;
    
    @Autowired
    private ReorderSuggestionService reorderSuggestionService;
    
    @EventListener
    @Async
    public void handleStockLevelChanged(StockLevelChangedEvent event) {
        try {
            if (event.isBelowReorderThreshold()) {
                logger.info("Stock level below reorder threshold for product ID {}: {} < {}", 
                           event.getProduct().getId(), 
                           event.getNewStockLevel(), 
                           event.getProduct().getReorderThreshold());
                
                // Check for duplicate PENDING suggestions
                if (!pricingSuggestionService.hasPendingSuggestionsForProduct(event.getProduct().getId(), TriggerReason.INVENTORY_LOW)) {
                    // Create both pricing and reorder suggestions asynchronously
                    createPricingSuggestionAsync(event.getProduct().getId(), TriggerReason.INVENTORY_LOW);
                    createReorderSuggestionAsync(event.getProduct().getId(), TriggerReason.INVENTORY_LOW);
                } else {
                    logger.info("Skipping duplicate PENDING suggestions for product ID {} with INVENTORY_LOW trigger", 
                               event.getProduct().getId());
                }
            }
        } catch (Exception e) {
            logger.error("Error handling stock level changed event for product ID {}: {}", 
                        event.getProduct().getId(), e.getMessage(), e);
        }
    }
    
    @EventListener
    @Async
    public void handleDemandVelocityChanged(DemandVelocityChangedEvent event) {
        try {
            if (event.isDemandSpike()) {
                logger.info("Demand spike detected for product ID {}: {} > 3x category average ({})", 
                           event.getProduct().getId(), 
                           event.getNewDemandVelocity(), 
                           event.getCategoryAverageDemand());
                
                // Check for duplicate PENDING suggestions
                if (!pricingSuggestionService.hasPendingSuggestionsForProduct(event.getProduct().getId(), TriggerReason.DEMAND_SPIKE)) {
                    // Create both pricing and reorder suggestions asynchronously
                    createPricingSuggestionAsync(event.getProduct().getId(), TriggerReason.DEMAND_SPIKE);
                    createReorderSuggestionAsync(event.getProduct().getId(), TriggerReason.DEMAND_SPIKE);
                } else {
                    logger.info("Skipping duplicate PENDING suggestions for product ID {} with DEMAND_SPIKE trigger", 
                               event.getProduct().getId());
                }
            }
        } catch (Exception e) {
            logger.error("Error handling demand velocity changed event for product ID {}: {}", 
                        event.getProduct().getId(), e.getMessage(), e);
        }
    }
    
    private void createPricingSuggestionAsync(Long productId, TriggerReason triggerReason) {
        try {
            PricingSuggestion suggestion = pricingSuggestionService.createPricingSuggestion(productId, triggerReason);
            logger.info("Created pricing suggestion ID {} for product ID {} with trigger reason {}", 
                       suggestion.getId(), productId, triggerReason);
        } catch (Exception e) {
            logger.error("Error creating pricing suggestion for product ID {}: {}", productId, e.getMessage(), e);
            // Silent drop is worse than a rule-based recommendation - but we've already logged the error
        }
    }
    
    private void createReorderSuggestionAsync(Long productId, TriggerReason triggerReason) {
        try {
            ReorderSuggestion suggestion = reorderSuggestionService.createReorderSuggestion(productId, triggerReason);
            logger.info("Created reorder suggestion ID {} for product ID {} with trigger reason {}", 
                       suggestion.getId(), productId, triggerReason);
        } catch (Exception e) {
            logger.error("Error creating reorder suggestion for product ID {}: {}", productId, e.getMessage(), e);
            // Silent drop is worse than a rule-based recommendation - but we've already logged the error
        }
    }
}