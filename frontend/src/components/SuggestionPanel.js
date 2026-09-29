import React from 'react';

function SuggestionPanel({ title, suggestions, onAccept, onReject }) {
  const getConfidenceClass = (confidence) => {
    if (confidence >= 0.8) return 'confidence-high';
    if (confidence >= 0.5) return 'confidence-medium';
    return 'confidence-low';
  };

  const getConfidenceText = (confidence) => {
    if (confidence >= 0.8) return 'High';
    if (confidence >= 0.5) return 'Medium';
    return 'Low';
  };

  const formatTriggerReason = (reason) => {
    switch (reason) {
      case 'INVENTORY_LOW':
        return 'Low Inventory';
      case 'DEMAND_SPIKE':
        return 'High Demand';
      case 'MANUAL':
        return 'Manual Request';
      default:
        return reason;
    }
  };

  return (
    <div className="suggestion-panel">
      <div className="panel-header">
        {title} ({suggestions.length})
      </div>
      <div className="suggestion-list">
        {suggestions.length === 0 ? (
          <div className="loading">No pending suggestions</div>
        ) : (
          suggestions.map((suggestion) => (
            <div className="suggestion-item" key={suggestion.id}>
              <div className="suggestion-header">
                <div className="suggestion-title">
                  {title.includes('Pricing') 
                    ? `New Price: $${suggestion.recommendedPrice?.toFixed(2) || 'N/A'}` 
                    : `Reorder: ${suggestion.recommendedQuantity || 0} units`}
                </div>
                <div className={getConfidenceClass(suggestion.confidence)}>
                  {getConfidenceText(suggestion.confidence)} ({Math.round(suggestion.confidence * 100)}%)
                </div>
              </div>
              
              <div className="suggestion-product">
                <strong>{suggestion.product?.name}</strong>
              </div>
              
              <div className="suggestion-details">
                <div>
                  <strong>Reason:</strong> {formatTriggerReason(suggestion.triggerReason)}
                </div>
                {suggestion.reasoning && (
                  <div>
                    <strong>AI Reasoning:</strong> {suggestion.reasoning}
                  </div>
                )}
              </div>
              
              <div className="suggestion-actions">
                <button 
                  className="btn-success" 
                  onClick={() => onAccept(suggestion.id)}
                >
                  Accept
                </button>
                <button 
                  className="btn-danger" 
                  onClick={() => onReject(suggestion.id)}
                >
                  Reject
                </button>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}

export default SuggestionPanel;