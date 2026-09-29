# ShopStream Reactive Commerce Advisor

This project implements a complete reactive commerce advisor system that automatically detects inventory signals and generates AI-powered pricing and replenishment recommendations.

## 🎯 Project Overview

ShopStream runs an online store with hundreds of SKUs across electronics, apparel, and home goods. Prices are set manually and reviewed weekly. Inventory levels update in real time as orders flow in — but pricing and replenishment decisions lag behind. 

This system solves that problem by:
1. **Automatically detecting** inventory signals (low stock, demand spikes)
2. **Generating AI-powered recommendations** for pricing and reorder quantities
3. **Presenting decisions** to merchandising teams for approval
4. **Updating products** when decisions are accepted

## ✅ Completed Tasks

### T-1: Domain Model
- Product entity with SKU, price, stock level, demand velocity
- Inventory snapshot tracking
- Pricing and reorder suggestions with state machines
- Comprehensive enums for business concepts

### T-2: Pluggable Commerce Engine
- Unified CommerceAdvisor interface
- Strategy pattern with PricingStrategy and ReorderStrategy
- Rule-based fallback implementations
- AI strategy implementations
- Runtime-configurable strategy selection

### T-3: AI Commerce Advisor
- Separate prompts for inventory-low vs demand-spike scenarios
- Structured context with product details and market data
- LLM integration with validation and fallback
- Streaming endpoint for real-time AI reasoning (bonus)

### T-4: Agentic Recommendation Loop
- Event-driven architecture with Spring Events
- Async processing with @Async listeners
- Duplicate prevention for suggestions
- Automatic fallback on AI failures

### T-5: Merchandising Console
- Product monitoring with current status
- Pending suggestions with confidence indicators
- Visual badge system for trigger reasons
- Accept/reject functionality

## 🚀 How to Run the System

### Backend (Java/Spring Boot)
1. The backend is already running on port 8080
2. API endpoints are available at `http://localhost:8080/api/*`

### Testing the System

Open the `DEMO_GUIDE.md` file for detailed instructions on how to test the complete system, including:

1. **Viewing current products**
2. **Triggering DEMAND_SPIKE events** by simulating sales
3. **Triggering INVENTORY_LOW events** by reducing stock levels
4. **Accepting/rejecting suggestions**
5. **Seeing real-time updates**

### API Endpoints

- `GET /api/products` - Get all products
- `POST /api/products/{id}/orders` - Simulate sale (increases demand velocity)
- `PATCH /api/products/{id}/stock` - Update stock level
- `GET /api/suggestions/pricing` - Get pricing suggestions
- `GET /api/suggestions/reorder` - Get reorder suggestions
- `PUT /api/suggestions/{type}/{id}` - Update suggestion status

## 🏗️ Technical Architecture

### Backend Structure
```
backend/demo/
├── src/main/java/com/inventory/demo/
│   ├── model/        # Domain entities
│   ├── enums/        # Business enumerations
│   ├── repository/   # Data access interfaces
│   ├── service/      # Business logic
│   ├── engine/       # Strategy pattern implementation
│   ├── events/       # Event-driven processing
│   ├── controller/   # REST API endpoints
│   └── ai/          # AI integration utilities
└── ADR.md           # Architectural decisions
```

### Frontend Structure
```
frontend/
├── src/
│   ├── components/   # React components
│   ├── App.js        # Main application
│   └── App.css       # Styling
└── README.md         # Frontend documentation
```

## 🎯 Business Value

This system provides significant business value by:
- **Automating reactive decisions** that previously required manual monitoring
- **Reducing response time** from hours/days to milliseconds
- **Improving consistency** through rule-based fallbacks when AI fails
- **Empowering merchandisers** with AI insights and confidence metrics
- **Preventing missed opportunities** through automatic alerting

## 📄 Documentation

- `PROJECT_SUMMARY.md` - Complete project overview
- `DEMO_GUIDE.md` - Step-by-step testing instructions
- `ADR.md` - Architectural decision records
- `README.md` files in backend and frontend directories

## 🧪 Testing the Agentic Loop

The complete end-to-end flow:
1. **Observe**: Inventory changes (stock levels, demand velocity)
2. **Reason**: System generates AI-powered recommendations
3. **Act**: Suggestions appear in the merchandising console
4. **Checkpoint**: Human merchandisers approve/reject decisions

Try the following to see it in action:
1. Send multiple `POST /api/products/1/orders` requests
2. Watch new suggestions appear in `GET /api/suggestions/*` 
3. Accept a pricing suggestion and see the product price update
4. Accept a reorder suggestion and see the stock level increase

Congratulations! You're now running ShopStream's reactive commerce advisor - automatically protecting revenue, optimizing inventory, and empowering merchandisers with intelligent recommendations.