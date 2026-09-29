# Architectural Decision Record (ADR)

## Title: Reactive Commerce Advisor with Agentic Recommendation Loop

## Status: Accepted

## Context
ShopStream needs a reactive commerce advisor that automatically detects inventory signals and generates AI-powered pricing and replenishment recommendations for merchandising approval. The system must handle two key scenarios:
1. Inventory low events (stock < reorder threshold)
2. Demand spike events (demand velocity > 3x category average)

The solution requires a pluggable architecture that supports both rule-based and AI strategies, with automatic triggering based on business conditions.

## Decision
We implemented a multi-layer architecture:

### Domain Model Layer
- `Product`: Core product entity with SKU, price, stock, demand velocity
- `InventorySnapshot`: Historical inventory tracking
- `PricingSuggestion`: Price recommendations with state machine (PENDING/ACCEPTED/REJECTED)
- `ReorderSuggestion`: Replenishment recommendations with state machine

### Engine Layer
- Unified `CommerceAdvisor` interface returning both pricing and reorder recommendations
- Strategy pattern with `PricingStrategy` and `ReorderStrategy` contracts
- Rule-based implementations as deterministic fallbacks
- AI implementations with LLM integration and validation
- Runtime-configurable strategy selection without restart

### Agentic Loop Layer
- Event-driven architecture using Spring `ApplicationEventPublisher`
- `StockLevelChangedEvent` and `DemandVelocityChangedEvent` for business signals
- `@Async` event listeners for non-blocking recommendation generation
- Duplicate detection to prevent redundant suggestions
- Automatic fallback to rule-based strategies on AI failures

### Integration Layer
- REST endpoints for manual suggestion generation
- Async event handlers for automatic triggering
- Shared contracts ensuring consistency across call paths

## Consequences

### Positive
- **Reactive automation**: Inventory signals automatically trigger appropriate actions
- **Extensible design**: New strategies (e.g., competitor-aware) plug in without changes
- **Robust error handling**: AI failures gracefully fall back to rule-based approaches
- **Duplicate prevention**: Smart deduplication prevents spamming merchandisers
- **Performance isolation**: AI calls operate off the critical request path

### Negative
- **Event complexity**: Multiple event types increase system complexity
- **State management**: Pending suggestions require careful lifecycle management

## Implementation Details

### T-2: Pluggable Commerce Engine
1. **Unified Interface**: Single `CommerceAdvisor` contract simplifies caller logic
2. **Strategy Pattern**: Clean separation between rule-based and AI implementations
3. **Runtime Configuration**: Dynamic strategy switching via REST endpoints
4. **Validation**: Both strategies validate outputs (positive prices, quantities)

### T-3: AI Commerce Advisor
1. **Context-Rich Prompts**: Separate prompts for inventory-low vs demand-spike scenarios
2. **Structured Responses**: JSON parsing with comprehensive validation
3. **Bounds Checking**: Price recommendations validated for business sanity
4. **Fallback Mechanism**: Rule-based strategies automatically engage on AI failures
5. **Streaming Endpoint**: Bonus feature for real-time AI reasoning visualization

### T-4: Agentic Recommendation Loop
1. **Event-Driven**: Stock/demand changes automatically publish events
2. **Async Processing**: Recommendations generated without blocking user requests
3. **Smart Deduplication**: Prevents multiple pending suggestions for same trigger
4. **Immediate Responsiveness**: Endpoints return instantly while background processing occurs
5. **Automatic Lifecycle Management**: Suggestions automatically update product prices/stock when accepted

## Future Extensions
- Competitor-aware strategies plug directly into existing contracts
- Additional trigger conditions (seasonality, promotions) follow same pattern
- Machine learning models for demand forecasting can enhance velocity calculations

This architecture successfully delivers ShopStream's reactive commerce advisor while establishing a foundation for future enhancements.