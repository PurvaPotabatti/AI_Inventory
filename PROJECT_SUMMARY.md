# ShopStream Reactive Commerce Advisor - Project Summary

This project implements a complete reactive commerce advisor system that automatically detects inventory signals and generates AI-powered pricing and replenishment recommendations.

## ✅ Completed Tasks

### T-1: Domain Model
- **Product entity** with SKU, price, stock level, demand velocity
- **Inventory snapshot** tracking
- **Pricing and reorder suggestions** with state machines (PENDING/ACCEPTED/REJECTED)
- **Enums** for product lifecycle, categories, suggestion status, and trigger reasons

### T-2: Pluggable Commerce Engine
- **Unified CommerceAdvisor interface** for both pricing and reorder recommendations
- **Strategy pattern** with PricingStrategy and ReorderStrategy contracts
- **Rule-based implementations** as deterministic fallbacks:
  - Pricing: 10% increase if stock < reorder threshold, 5% increase if demand > 2x category average, otherwise HOLD
  - Reorder: Quantity = (reorder threshold × 3) − current stock, minimum 1
- **AI strategy implementations** with LLM integration
- **Runtime-configurable strategy selection** without restart

### T-3: AI Commerce Advisor
- **Separate prompts** for inventory-low vs demand-spike scenarios
- **Structured context** including product details, stock levels, demand velocity, and category averages
- **Validation of LLM responses** (positive prices, positive quantities, valid confidence scores)
- **Graceful fallback** to rule-based strategies on timeouts, errors, or invalid responses
- **Streaming endpoint** for real-time AI reasoning (bonus feature)

### T-4: Agentic Recommendation Loop
- **Event-driven architecture** using Spring ApplicationEventPublisher
- **StockLevelChangedEvent** and **DemandVelocityChangedEvent** for business signals
- **@Async event listeners** for non-blocking recommendation generation
- **Duplicate detection** to prevent redundant suggestions
- **Automatic fallback** to rule-based strategies on AI failures
- **Immediate responsiveness** - endpoints return instantly while background processing occurs

### T-5: Merchandising Console
- **Product monitoring** with current stock levels, prices, and demand velocity
- **Pending suggestions panel** with confidence levels and AI reasoning
- **Visual badge system** for different trigger reasons (LOW STOCK, HIGH DEMAND, MANUAL)
- **Action buttons** for simulating sales and editing stock levels
- **Accept/reject functionality** for merchandising decisions
- **Real-time updates** with auto-refresh and manual refresh options
## 🏗️ Architecture Overview

### Backend (Spring Boot)
```
Domain Layer
├── Product, PricingSuggestion, ReorderSuggestion
├── Enums (Category, SuggestionStatus, TriggerReason, etc.)

Engine Layer
├── CommerceAdvisor Interface
├── PricingStrategy & ReorderStrategy Interfaces
├── Rule-based Strategy Implementations
├── AI Strategy Implementations
└── Strategy Configuration

Events Layer
├── StockLevelChangedEvent
├── DemandVelocityChangedEvent
├── RecommendationEventListener (@Async)
└── Event Configuration

Service Layer
├── ProductService (with event publishing)
├── PricingSuggestionService
├── ReorderSuggestionService
└── CommerceAdvisor Implementation

Controller Layer
├── ProductController
├── SuggestionController
├── ConfigurationController
## 🔧 Key Technical Features

### Backend
- **Event-Driven Architecture**: Automatically responds to business conditions
- **Async Processing**: Non-blocking suggestion generation
- **Pluggable Strategies**: Easy to extend with new algorithms
- **Runtime Configuration**: Switch strategies without restart
- **Error Handling**: Graceful degradation to rule-based approaches
- **Duplicate Prevention**: Smart deduplication logic
- **Streaming API**: Real-time AI reasoning (bonus feature)
## 🚀 How to Run

### Backend
1. Navigate to `backend/demo`
2. Run `./mvnw spring-boot:run`
3. API available at http://localhost:8080

### Frontend
1. Navigate to `frontend`
2. Run `npm install` (if dependencies aren't installed)
3. Run `npm start`
4. UI available at http://localhost:3000

## 🧪 Testing the Complete Flow

1. **Start both applications**
2. **Create products** via backend API or database
3. **Monitor the console** to see products listed
4. **Trigger events**:
   - Reduce stock below reorder threshold to trigger LOW STOCK suggestions
   - Simulate multiple sales to trigger HIGH DEMAND suggestions
   - Generate manual suggestions via the "Generate Suggestion" buttons
5. **Watch suggestions appear** in the sidebar panels
6. **Review AI reasoning** and confidence scores
7. **Make decisions** by accepting or rejecting suggestions
8. **See updates** reflected in product prices/stock levels

## 📄 Documentation

### ADR (Architectural Decision Record)
Detailed documentation of all architectural decisions, implementation choices, and tradeoffs made during development.

### README Files
- Backend README: Technical overview and API documentation
- Frontend README: Component descriptions and usage instructions

## 🎯 Business Value Delivered

This system provides significant business value by:
- **Automating reactive decisions** that previously required manual monitoring
- **Reducing response time** from hours/days to milliseconds
- **Improving consistency** through rule-based fallbacks when AI fails
- **Empowering merchandisers** with AI insights and confidence metrics
- **Preventing missed opportunities** through automatic alerting
- **Supporting future extensions** like competitor-aware pricing

The implementation successfully delivers ShopStream's reactive commerce advisor while establishing a foundation for future enhancements and AI improvements.

### Frontend
- **Responsive Design**: Works on various screen sizes
- **Real-time Updates**: Auto-refresh with manual override
- **Visual Feedback**: Color-coded badges and confidence indicators
- **Intuitive Controls**: Inline editing and action buttons
- **Error Handling**: User-friendly error messages
- **Clean State Management**: React hooks for data flow
└── StreamingSuggestionController (SSE)
```

### Frontend (React)
```
Components
├── MerchandisingConsole (main orchestration)
├── ProductList (product monitoring)
├── SuggestionPanel (pricing/reorder suggestions)
└── Styling (CSS modules)

Features
├── Real-time data polling
├── Inline stock editing
├── Sale simulation
├── Suggestion acceptance/rejection
└── Visual indicators/badges
```