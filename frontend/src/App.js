import React, { useState, useEffect } from 'react';
import axios from 'axios';
import ProductList from './components/ProductList';
import SuggestionPanel from './components/SuggestionPanel';
import './App.css';

const API_BASE_URL = 'http://localhost:8080/api';

function MerchandisingConsole() {
  const [products, setProducts] = useState([]);
  const [pricingSuggestions, setPricingSuggestions] = useState([]);
  const [reorderSuggestions, setReorderSuggestions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Fetch all data
  const fetchData = async () => {
    setLoading(true);
    setError('');
    try {
      const [productsRes, pricingRes, reorderRes] = await Promise.all([
        axios.get(`${API_BASE_URL}/products`),
        axios.get(`${API_BASE_URL}/suggestions/pricing`),
        axios.get(`${API_BASE_URL}/suggestions/reorder`)
      ]);
      
      setProducts(productsRes.data);
      setPricingSuggestions(pricingRes.data);
      setReorderSuggestions(reorderRes.data);
    } catch (err) {
      setError('Failed to fetch data: ' + err.message);
      console.error('Error fetching data:', err);
    } finally {
      setLoading(false);
    }
  };

  // Simulate a sale for a product
  const simulateSale = async (productId) => {
    try {
      await axios.post(`${API_BASE_URL}/products/${productId}/orders`);
      fetchData(); // Refresh data after sale
    } catch (err) {
      setError('Failed to simulate sale: ' + err.message);
    }
  };

  // Update stock level for a product
  const updateStock = async (productId, stockLevel) => {
    try {
      await axios.patch(`${API_BASE_URL}/products/${productId}/stock`, { stockLevel });
      fetchData(); // Refresh data after stock update
    } catch (err) {
      setError('Failed to update stock: ' + err.message);
    }
  };

  // Update suggestion status
  const updateSuggestionStatus = async (type, suggestionId, status) => {
    try {
      await axios.put(`${API_BASE_URL}/suggestions/${type}/${suggestionId}`, { status });
      fetchData(); // Refresh data after status update
    } catch (err) {
      setError(`Failed to update ${type} suggestion: ` + err.message);
    }
  };

  // Accept suggestion
  const acceptSuggestion = (type, suggestionId) => {
    updateSuggestionStatus(type, suggestionId, 'ACCEPTED');
  };

  // Reject suggestion
  const rejectSuggestion = (type, suggestionId) => {
    updateSuggestionStatus(type, suggestionId, 'REJECTED');
  };

  // Load data on component mount
  useEffect(() => {
    fetchData();
    // Poll for updates every 30 seconds
    const interval = setInterval(fetchData, 30000);
    return () => clearInterval(interval);
  }, []);

  return (
    <div className="merchandising-console">
      <header className="console-header">
        <h1>ShopStream Merchandising Console</h1>
        <button onClick={fetchData} disabled={loading}>
          {loading ? 'Refreshing...' : 'Refresh Data'}
        </button>
      </header>

      {error && (
        <div className="error-banner">
          Error: {error}
          <button onClick={() => setError('')} className="close-btn">✕</button>
        </div>
      )}

      <div className="console-content">
        <div className="main-panel">
          <ProductList 
            products={products}
            onSimulateSale={simulateSale}
            onUpdateStock={updateStock}
          />
        </div>
        
        <div className="sidebar">
          <SuggestionPanel 
            title="Pricing Suggestions"
            suggestions={pricingSuggestions.filter(s => s.status === 'PENDING')}
            onAccept={(id) => acceptSuggestion('pricing', id)}
            onReject={(id) => rejectSuggestion('pricing', id)}
          />
          
          <SuggestionPanel 
            title="Reorder Suggestions"
            suggestions={reorderSuggestions.filter(s => s.status === 'PENDING')}
            onAccept={(id) => acceptSuggestion('reorder', id)}
            onReject={(id) => rejectSuggestion('reorder', id)}
          />
        </div>
      </div>
    </div>
  );
}

export default MerchandisingConsole;