package com.inventory.demo.controller;

import com.inventory.demo.engine.impl.CommerceAdvisorImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/config")
public class ConfigurationController {
    
    @Autowired
    private CommerceAdvisorImpl commerceAdvisor;
    
    @GetMapping("/strategies")
    public StrategyInfo getAvailableStrategies() {
        return new StrategyInfo(
            commerceAdvisor.getAvailablePricingStrategies(),
            commerceAdvisor.getAvailableReorderStrategies(),
            commerceAdvisor.getActivePricingStrategy(),
            commerceAdvisor.getActiveReorderStrategy()
        );
    }
    
    @PostMapping("/strategies/pricing")
    public String setPricingStrategy(@RequestParam String strategyName) {
        try {
            commerceAdvisor.setActivePricingStrategy(strategyName);
            return "Pricing strategy successfully set to: " + strategyName;
        } catch (IllegalArgumentException e) {
            return "Error: " + e.getMessage();
        }
    }
    
    @PostMapping("/strategies/reorder")
    public String setReorderStrategy(@RequestParam String strategyName) {
        try {
            commerceAdvisor.setActiveReorderStrategy(strategyName);
            return "Reorder strategy successfully set to: " + strategyName;
        } catch (IllegalArgumentException e) {
            return "Error: " + e.getMessage();
        }
    }
    
    // Inner class for response DTO
    public static class StrategyInfo {
        private String[] pricingStrategies;
        private String[] reorderStrategies;
        private String activePricingStrategy;
        private String activeReorderStrategy;
        
        public StrategyInfo(String[] pricingStrategies, String[] reorderStrategies, 
                          String activePricingStrategy, String activeReorderStrategy) {
            this.pricingStrategies = pricingStrategies;
            this.reorderStrategies = reorderStrategies;
            this.activePricingStrategy = activePricingStrategy;
            this.activeReorderStrategy = activeReorderStrategy;
        }
        
        // Getters
        public String[] getPricingStrategies() { return pricingStrategies; }
        public String[] getReorderStrategies() { return reorderStrategies; }
        public String getActivePricingStrategy() { return activePricingStrategy; }
        public String getActiveReorderStrategy() { return activeReorderStrategy; }
    }
}