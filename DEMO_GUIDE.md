# ShopStream Reactive Commerce Advisor - Demonstration Guide

## System Status
✅ Backend is running on port 8080
✅ API endpoints are accessible
✅ Agentic recommendation loop is active

## How to Test the Complete System

### 1. View Current Products
Open your browser and navigate to:
```
http://localhost:8080/api/products
```

You should see a list of products with their current prices, stock levels, and demand velocities.

### 2. Test the Agentic Loop - Method 1: Simulate Sales
To trigger a DEMAND_SPIKE event:
```
POST http://localhost:8080/api/products/1/orders
```

Repeat this request several times (5-10 times) for the same product to increase its demand velocity above the threshold.

After 3-5 requests, check suggestions:
```
GET http://localhost:8080/api/suggestions/pricing
GET http://localhost:8080/api/suggestions/reorder
```

You should see new suggestions with triggerReason "DEMAND_SPIKE".

### 3. Test the Agentic Loop - Method 2: Reduce Stock Levels
To trigger an INVENTORY_LOW event:
```
PATCH http://localhost:8080/api/products/1/stock
Body: { "stockLevel": 2 }
```

Choose a product with a higher reorder threshold (check the product details first).
Set the stock level below the reorder threshold.

Check suggestions immediately:
```
GET http://localhost:8080/api/suggestions/pricing
GET http://localhost:8080/api/suggestions/reorder
```

You should see new suggestions with triggerReason "INVENTORY_LOW".

### 4. Accept/Reject Suggestions
To accept a suggestion:
```
PUT http://localhost:8080/api/suggestions/pricing/1
Body: { "status": "ACCEPTED" }
```

To reject a suggestion:
```
PUT http://localhost:8080/api/suggestions/reorder/1
Body: { "status": "REJECTED" }
```

When a pricing suggestion is accepted, the product's currentPrice will update.
When a reorder suggestion is accepted, the product's stockLevel will increase.

### 5. View the Merchandising Console
Open the demo.html file in your browser to see a simplified version of the merchandising console that shows:
- Product list with current information
- Pending suggestions with accept/reject buttons
- Real-time updates when you make changes

## Key Features Demonstrated

1. **Event-Driven Automation**: No manual triggering needed - suggestions appear automatically
2. **Duplicate Prevention**: Multiple triggers won't create duplicate suggestions
3. **AI-Powered Recommendations**: Real LLM integration with context-aware prompts
4. **Robust Error Handling**: Falls back to rule-based strategies when AI fails
5. **Real-Time Updates**: Changes are immediately reflected in the system
6. **Complete Audit Trail**: All actions are tracked with proper status updates

## Expected Results

- DEMAND_SPIKE suggestions should recommend modest price increases to capitalize on popularity
- INVENTORY_LOW suggestions should recommend price increases to preserve scarce inventory
- Reorder suggestions should calculate optimal quantities based on current stock and thresholds
- All suggestions include confidence scores and plain-English reasoning
- Merchandising decisions are properly recorded and affect product data

Congratulations! You're now seeing ShopStream's reactive commerce advisor in action - automatically protecting revenue, optimizing inventory, and empowering merchandisers with intelligent recommendations.