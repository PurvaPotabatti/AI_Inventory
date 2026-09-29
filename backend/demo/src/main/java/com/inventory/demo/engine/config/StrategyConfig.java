package com.inventory.demo.engine.config;

import com.inventory.demo.engine.PricingStrategy;
import com.inventory.demo.engine.ReorderStrategy;
import com.inventory.demo.engine.strategy.RuleBasedPricingStrategy;
import com.inventory.demo.engine.strategy.RuleBasedReorderStrategy;
import com.inventory.demo.engine.strategy.AiPricingStrategy;
import com.inventory.demo.engine.strategy.AiReorderStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StrategyConfig {
    
    @Bean("ruleBasedPricingStrategy")
    public PricingStrategy ruleBasedPricingStrategy() {
        return new RuleBasedPricingStrategy();
    }
    
    @Bean("ruleBasedReorderStrategy")
    public ReorderStrategy ruleBasedReorderStrategy() {
        return new RuleBasedReorderStrategy();
    }
    
    @Bean("aiPricingStrategy")
    public PricingStrategy aiPricingStrategy() {
        return new AiPricingStrategy();
    }
    
    @Bean("aiReorderStrategy")
    public ReorderStrategy aiReorderStrategy() {
        return new AiReorderStrategy();
    }
}