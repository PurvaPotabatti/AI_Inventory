package com.inventory.demo.engine.impl;

import com.inventory.demo.engine.CommerceAdvisor;
import com.inventory.demo.engine.PricingStrategy;
import com.inventory.demo.engine.ReorderStrategy;
import com.inventory.demo.model.Product;
import com.inventory.demo.model.PricingSuggestion;
import com.inventory.demo.model.ReorderSuggestion;
import com.inventory.demo.enums.TriggerReason;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class CommerceAdvisorImpl implements CommerceAdvisor {
    
    private final Map<String, PricingStrategy> pricingStrategies;
    private final Map<String, ReorderStrategy> reorderStrategies;
    private volatile String activePricingStrategy;
    private volatile String activeReorderStrategy;
    
    @Autowired
    public CommerceAdvisorImpl(
            @Qualifier("ruleBasedPricingStrategy") PricingStrategy ruleBasedPricingStrategy,
            @Qualifier("aiPricingStrategy") PricingStrategy aiPricingStrategy,
            @Qualifier("ruleBasedReorderStrategy") ReorderStrategy ruleBasedReorderStrategy,
            @Qualifier("aiReorderStrategy") ReorderStrategy aiReorderStrategy) {
        
        this.pricingStrategies = new ConcurrentHashMap<>();
        this.reorderStrategies = new ConcurrentHashMap<>();
        
        // Register all available strategies
        pricingStrategies.put("ruleBasedPricingStrategy", ruleBasedPricingStrategy);
        pricingStrategies.put("aiPricingStrategy", aiPricingStrategy);
        
        reorderStrategies.put("ruleBasedReorderStrategy", ruleBasedReorderStrategy);
        reorderStrategies.put("aiReorderStrategy", aiReorderStrategy);
        
        // Set defaults
        this.activePricingStrategy = "ruleBasedPricingStrategy";
        this.activeReorderStrategy = "ruleBasedReorderStrategy";
    }
    
    @Override
    public PricingSuggestion generatePricingSuggestion(Product product, TriggerReason triggerReason) {
        PricingStrategy strategy = pricingStrategies.get(activePricingStrategy);
        if (strategy == null) {
            throw new IllegalStateException("No pricing strategy found for: " + activePricingStrategy);
        }
        return strategy.generatePricingSuggestion(product, triggerReason);
    }
    
    @Override
    public ReorderSuggestion generateReorderSuggestion(Product product, TriggerReason triggerReason) {
        ReorderStrategy strategy = reorderStrategies.get(activeReorderStrategy);
        if (strategy == null) {
            throw new IllegalStateException("No reorder strategy found for: " + activeReorderStrategy);
        }
        return strategy.generateReorderSuggestion(product, triggerReason);
    }
    
    public void setActivePricingStrategy(String strategyName) {
        if (!pricingStrategies.containsKey(strategyName)) {
            throw new IllegalArgumentException("Unknown pricing strategy: " + strategyName);
        }
        this.activePricingStrategy = strategyName;
    }
    
    public void setActiveReorderStrategy(String strategyName) {
        if (!reorderStrategies.containsKey(strategyName)) {
            throw new IllegalArgumentException("Unknown reorder strategy: " + strategyName);
        }
        this.activeReorderStrategy = strategyName;
    }
    
    public String getActivePricingStrategy() {
        return this.activePricingStrategy;
    }
    
    public String getActiveReorderStrategy() {
        return this.activeReorderStrategy;
    }
    
    public String[] getAvailablePricingStrategies() {
        return pricingStrategies.keySet().toArray(new String[0]);
    }
    
    public String[] getAvailableReorderStrategies() {
        return reorderStrategies.keySet().toArray(new String[0]);
    }
}