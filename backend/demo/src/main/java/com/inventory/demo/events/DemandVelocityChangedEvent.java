package com.inventory.demo.events;

import com.inventory.demo.model.Product;
import org.springframework.context.ApplicationEvent;

public class DemandVelocityChangedEvent extends ApplicationEvent {
    private final Product product;
    private final Integer oldDemandVelocity;
    private final Integer newDemandVelocity;
    private final int categoryAverageDemand;
    
    public DemandVelocityChangedEvent(Object source, Product product, 
                                    Integer oldDemandVelocity, Integer newDemandVelocity,
                                    int categoryAverageDemand) {
        super(source);
        this.product = product;
        this.oldDemandVelocity = oldDemandVelocity;
        this.newDemandVelocity = newDemandVelocity;
        this.categoryAverageDemand = categoryAverageDemand;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public Integer getOldDemandVelocity() {
        return oldDemandVelocity;
    }
    
    public Integer getNewDemandVelocity() {
        return newDemandVelocity;
    }
    
    public int getCategoryAverageDemand() {
        return categoryAverageDemand;
    }
    
    public boolean isDemandSpike() {
        // Demand spike is when velocity exceeds 3x category average
        return newDemandVelocity > 3 * categoryAverageDemand;
    }
}