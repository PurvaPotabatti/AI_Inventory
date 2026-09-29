package com.inventory.demo.engine;

import com.inventory.demo.model.Product;
import com.inventory.demo.model.ReorderSuggestion;
import com.inventory.demo.enums.TriggerReason;

public interface ReorderStrategy {
    ReorderSuggestion generateReorderSuggestion(Product product, TriggerReason triggerReason);
}