# ShopStream Merchandising Console

This is the frontend for the ShopStream reactive commerce advisor system. It provides a user interface for merchandisers to monitor products, view AI-generated suggestions, and make pricing/reorder decisions.

## Features

### Product Monitoring
- View all products with their current stock levels, prices, and demand velocity
- See product lifecycle status (ACTIVE, OUT_OF_STOCK, etc.)
- Simulate sales to trigger demand spike events
- Manually update stock levels

### Suggestion Management
- View pending pricing and reorder suggestions
- See confidence levels and AI reasoning for each suggestion
- Accept or reject suggestions with one click
- Visual indicators for different trigger reasons (LOW STOCK, HIGH DEMAND, MANUAL)

### Real-time Updates
- Auto-refresh every 30 seconds
- Manual refresh button
- Error handling and user feedback

## Components

### Main Components
1. **ProductList** - Displays all products in a sortable table
2. **SuggestionPanel** - Shows pricing and reorder suggestions with action buttons
3. **MerchandisingConsole** - Main application component that orchestrates data flow

### Key Features
- **Badge System**: Color-coded badges for different trigger reasons
- **Inline Editing**: Edit stock levels directly in the product table
- **Action Buttons**: Quick actions for common operations
- **Responsive Design**: Works on different screen sizes

## Technical Details

### Technologies Used
- React 18
- Axios for API communication
- CSS for styling

### API Integration
Connects to the backend REST API at `http://localhost:8080/api` with endpoints for:
- Products: `/api/products`
- Pricing Suggestions: `/api/suggestions/pricing`
- Reorder Suggestions: `/api/suggestions/reorder`

## Running the Application

1. Make sure the backend is running on port 8080
2. Install dependencies: `npm install`
3. Start the development server: `npm start`
4. Open http://localhost:3000 in your browser

## Testing the Agentic Loop

1. Start both backend and frontend applications
2. Create products via the backend API or database
3. Use the "Simulate Sale" button to increase demand velocity
4. Use the stock edit functionality to reduce stock levels
5. Watch as suggestions automatically appear in the sidebar panels
6. Accept/reject suggestions to see the product updates