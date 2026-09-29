# ShopStream Reactive Commerce Advisor

This is a reactive commerce advisor system that automatically detects inventory signals and generates AI-powered pricing and replenishment recommendations.

## Features

### T-1: Domain Model
- Product entity with SKU, price, stock level, demand velocity
- Inventory snapshot tracking
- Pricing and reorder suggestions with state machines

### T-2: Pluggable Commerce Engine
- Unified CommerceAdvisor interface for both pricing and reorder recommendations
- Strategy pattern with PricingStrategy and ReorderStrategy contracts
- Rule-based implementations as deterministic fallbacks:
  - Pricing: 10% increase if stock < reorder threshold, 5% increase if demand > 2x category average, otherwise HOLD
  - Reorder: Quantity = (reorder threshold × 3) − current stock, minimum 1
- AI strategy implementations with LLM integration
- Runtime-configurable strategy selection without restart

### T-3: AI Commerce Advisor
- Separate prompts for inventory-low vs demand-spike scenarios
- Structured context including product details, stock levels, demand velocity, and category averages
- Validation of LLM responses (positive prices, positive quantities, valid confidence scores)
- Graceful fallback to rule-based strategies on timeouts, errors, or invalid responses
- Streaming endpoint for real-time AI reasoning (bonus feature)

### T-4: Agentic Recommendation Loop
- Event-driven architecture using Spring ApplicationEventPublisher
- StockLevelChangedEvent and DemandVelocityChangedEvent for business signals
- @Async event listeners for non-blocking recommendation generation
- Duplicate detection to prevent redundant suggestions
- Automatic fallback to rule-based strategies on AI failures
- Immediate responsiveness - endpoints return instantly while background processing occurs

## API Endpoints

### Products
- `POST /api/products` - Create a new product
- `GET /api/products` - Get all products (with optional lifecycle/category filters)
- `GET /api/products/{id}` - Get a specific product
- `PATCH /api/products/{id}/stock` - Update stock level
- `POST /api/products/{id}/orders` - Simulate a sale (increment demand velocity)

### Suggestions
- `POST /api/products/{id}/suggest-pricing` - Generate pricing suggestion manually
- `POST /api/products/{id}/suggest-reorder` - Generate reorder suggestion manually
- `GET /api/suggestions/pricing` - Get all pricing suggestions
- `GET /api/suggestions/reorder` - Get all reorder suggestions
- `PUT /api/suggestions/pricing/{id}` - Update pricing suggestion status
- `PUT /api/suggestions/reorder/{id}` - Update reorder suggestion status

### Configuration
- `GET /api/config/strategies` - Get available strategies
- `POST /api/config/strategies/pricing?strategyName={name}` - Switch pricing strategy
- `POST /api/config/strategies/reorder?strategyName={name}` - Switch reorder strategy

### Streaming
- `POST /api/products/{id}/suggest-pricing/stream` - Stream AI reasoning for pricing suggestions (SSE)

## Architecture

The system follows a layered architecture:

1. **Domain Layer**: Entities and enums representing the business domain
2. **Engine Layer**: Pluggable strategy implementations for pricing and reorder decisions
3. **Service Layer**: Business logic coordination and state management
4. **Event Layer**: Agentic recommendation loop with event-driven processing
5. **Controller Layer**: REST API endpoints and request handling

## Getting Started

1. Clone the repository
2. Run `./mvnw spring-boot:run` to start the application
3. Access the API at http://localhost:8080

## Testing the Recommendation Loop

1. Create a product: `POST /api/products`
2. Reduce stock below reorder threshold: `PATCH /api/products/{id}/stock`
3. Or simulate multiple sales to trigger demand spike: `POST /api/products/{id}/orders`
4. Check suggestions appear automatically: `GET /api/suggestions/pricing` and `GET /api/suggestions/reorder`
5. Accept suggestions to update product prices/stock levels: `PUT /api/suggestions/{type}/{id}`

## Technologies Used

- Java 17
- Spring Boot 3.x
- Spring Data JPA
- H2 Database (in-memory)
- Maven for dependency management