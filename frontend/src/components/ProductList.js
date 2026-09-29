import React, { useState } from 'react';

function ProductList({ products, onSimulateSale, onUpdateStock }) {
  const [editingStock, setEditingStock] = useState(null);
  const [newStockValue, setNewStockValue] = useState('');

  const handleEditStock = (productId, currentStock) => {
    setEditingStock(productId);
    setNewStockValue(currentStock.toString());
  };

  const handleSaveStock = (productId) => {
    const stockValue = parseInt(newStockValue);
    if (!isNaN(stockValue) && stockValue >= 0) {
      onUpdateStock(productId, stockValue);
      setEditingStock(null);
      setNewStockValue('');
    }
  };

  const handleCancelEdit = () => {
    setEditingStock(null);
    setNewStockValue('');
  };

  const getTriggerBadge = (triggerReason) => {
    switch (triggerReason) {
      case 'INVENTORY_LOW':
        return <span className="badge badge-inventory-low">LOW STOCK</span>;
      case 'DEMAND_SPIKE':
        return <span className="badge badge-demand-spike">HIGH DEMAND</span>;
      case 'MANUAL':
        return <span className="badge badge-manual">MANUAL</span>;
      default:
        return <span className="badge">{triggerReason}</span>;
    }
  };

  const getStatusColor = (lifecycle) => {
    switch (lifecycle) {
      case 'ACTIVE':
        return '#28a745';
      case 'OUT_OF_STOCK':
        return '#dc3545';
      case 'DISCONTINUED':
        return '#6c757d';
      default:
        return '#000';
    }
  };

  return (
    <div className="product-list">
      <table>
        <thead>
          <tr>
            <th>Product</th>
            <th>SKU</th>
            <th>Price</th>
            <th>Stock</th>
            <th>Demand Velocity</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {products.map((product) => (
            <tr key={product.id}>
              <td>
                <div><strong>{product.name}</strong></div>
                <div style={{ fontSize: '12px', color: '#666' }}>{product.category}</div>
              </td>
              <td>{product.sku}</td>
              <td>${product.currentPrice?.toFixed(2) || 'N/A'}</td>
              <td>
                {editingStock === product.id ? (
                  <div>
                    <input
                      type="number"
                      value={newStockValue}
                      onChange={(e) => setNewStockValue(e.target.value)}
                      min="0"
                      style={{ width: '60px' }}
                    />
                    <div className="action-buttons">
                      <button 
                        className="btn-success" 
                        onClick={() => handleSaveStock(product.id)}
                        style={{ marginLeft: '5px' }}
                      >
                        Save
                      </button>
                      <button 
                        className="btn-secondary" 
                        onClick={handleCancelEdit}
                      >
                        Cancel
                      </button>
                    </div>
                  </div>
                ) : (
                  <div>
                    {product.stockLevel}
                    <button 
                      className="btn-secondary" 
                      onClick={() => handleEditStock(product.id, product.stockLevel)}
                      style={{ marginLeft: '5px' }}
                    >
                      Edit
                    </button>
                  </div>
                )}
              </td>
              <td>{product.demandVelocity}</td>
              <td>
                <span style={{ color: getStatusColor(product.lifecycle) }}>
                  {product.lifecycle}
                </span>
              </td>
              <td>
                <div className="action-buttons">
                  <button 
                    className="btn-primary" 
                    onClick={() => onSimulateSale(product.id)}
                  >
                    Simulate Sale
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default ProductList;