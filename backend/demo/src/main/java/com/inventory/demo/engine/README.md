# Pluggable Commerce Engine

## Overview

This module implements a pluggable commerce engine for ShopStream's reactive commerce advisor. The engine provides strategy interfaces for pricing and reorder recommendations that can be switched at runtime without restarting the application.

## Architecture

The engine follows a Strategy pattern with the following components:

1. **Interfaces**: 
   - `PricingStrategy` - Contract for generating pricing recommendations
   - `ReorderStrategy` - Contract for generating reorder recommendations
   - `CommerceAdvisor` - Unified interface combining both strategies

2. **Implementations**:
   - `RuleBasedPricingStrategy` - Rule-based pricing logic (fallback)
   - `RuleBasedReorderStrategy` - Rule-based reorder logic (baseline)
   - `AiPricingStrategy` - Placeholder for AI-powered pricing (future)
   - `AiReorderStrategy` - Placeholder for AI-powered reordering (future)

3. **Runtime Management**:
   - `CommerceAdvisorImpl` - Manages active strategies and delegation
   - `StrategyConfig` - Spring configuration for strategy beans
   - `ConfigurationController` - REST endpoints for runtime strategy switching

## Usage

### API Endpoints

#### Get Available Strategies
```http
GET /api/config/strategies
```

Response:
```json
{
  "pricingStrategies": ["ruleBasedPricingStrategy", "aiPricingStrategy"],
  "reorderStrategies": ["ruleBasedReorderStrategy", "aiReorderStrategy"],
  "activePricingStrategy": "ruleBasedPricingStrategy",
  "activeReorderStrategy": "ruleBasedReorderStrategy"
}
```

#### Set Pricing Strategy
```http
POST /api/config/strategies/pricing?strategyName=aiPricingStrategy
```

#### Set Reorder Strategy
```http
POST /api/config/strategies/reorder?strategyName=aiReorderStrategy
```

### Integration with Services

Services can inject the `CommerceAdvisor` and use it to generate recommendations:

```java
@Autowired
private CommerceAdvisor commerceAdvisor;

// Generate pricing suggestion
PricingSuggestion pricingSuggestion = commerceAdvisor.generatePricingSuggestion(product, triggerReason);

// Generate reorder suggestion
ReorderSuggestion reorderSuggestion = commerceAdvisor.generateReorderSuggestion(product, triggerReason);
```

## Adding New Strategies

To add a new pricing strategy:

1. Implement the `PricingStrategy` interface
2. Register the bean in `StrategyConfig`
3. The strategy will be automatically available for runtime switching

Example:
```java
@Component
public class CompetitorAwarePricingStrategy implements PricingStrategy {
    @Override
    public PricingSuggestion generatePricingSuggestion(Product product, TriggerReason triggerReason) {
        // Implementation here
    }
}

// In StrategyConfig.java
@Bean("competitorAwarePricingStrategy")
public PricingStrategy competitorAwarePricingStrategy() {
    return new CompetitorAwarePricingStrategy();
}
```

## Rule-Based Logic

### Pricing Rules
- If stock < reorder threshold: recommend 10% price increase
- If demand velocity > 2× category average: recommend 5% increase
- Otherwise: HOLD current price

### Reorder Rules
- Recommend quantity = (reorder threshold × 3) − current stock
- Minimum recommended quantity: 1 unit

## Future Enhancements

The AI strategy placeholders (`AiPricingStrategy`, `AiReorderStrategy`) will be enhanced in Sprint 3 to integrate with actual LLM services for intelligent recommendation generation.