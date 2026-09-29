package com.inventory.demo.events;

import com.inventory.demo.model.Product;
import org.springframework.context.ApplicationEvent;

public class StockLevelChangedEvent extends ApplicationEvent {
    private final Product product;
    private final Integer oldStockLevel;
    private final Integer newStockLevel;
    
    public StockLevelChangedEvent(Object source, Product product, Integer oldStockLevel, Integer newStockLevel) {
        super(source);
        this.product = product;
        this.oldStockLevel = oldStockLevel;
        this.newStockLevel = newStockLevel;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public Integer getOldStockLevel() {
        return oldStockLevel;
    }
    
    public Integer getNewStockLevel() {
        return newStockLevel;
    }
    
    public boolean isBelowReorderThreshold() {
        return newStockLevel < product.getReorderThreshold();
    }
}