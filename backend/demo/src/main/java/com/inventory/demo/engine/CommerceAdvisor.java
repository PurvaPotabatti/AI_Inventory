package com.inventory.demo.engine;

import com.inventory.demo.model.Product;
import com.inventory.demo.model.PricingSuggestion;
import com.inventory.demo.model.ReorderSuggestion;
import com.inventory.demo.enums.TriggerReason;

public interface CommerceAdvisor {
    PricingSuggestion generatePricingSuggestion(Product product, TriggerReason triggerReason);
    ReorderSuggestion generateReorderSuggestion(Product product, TriggerReason triggerReason);
}