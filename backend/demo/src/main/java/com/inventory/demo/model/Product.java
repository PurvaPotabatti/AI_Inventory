package com.inventory.demo.model;

import com.inventory.demo.enums.Category;
import com.inventory.demo.enums.ProductLifecycle;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String sku;
    
    @Column(nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;
    
    @Column(name = "current_price", nullable = false)
    private BigDecimal currentPrice;
    
    @Column(name = "stock_level", nullable = false)
    private Integer stockLevel;
    
    @Column(name = "reorder_threshold", nullable = false)
    private Integer reorderThreshold;
    
    @Column(name = "demand_velocity", nullable = false)
    private Integer demandVelocity;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductLifecycle lifecycle;
    
    // Extension fields for Sprint 2
    @Column(name = "cost_price")
    private BigDecimal costPrice;
    
    @Column(name = "supplier_id")
    private Long supplierId;
    
    public boolean isBelowReorderThreshold() {
        return this.stockLevel < this.reorderThreshold;
    }
    
    public boolean isOutOfStock() {
        return this.stockLevel <= 0;
    }
    
    @PrePersist
    @PreUpdate
    private void updateLifecycle() {
        if (isOutOfStock()) {
            this.lifecycle = ProductLifecycle.OUT_OF_STOCK;
        } else if (this.lifecycle == ProductLifecycle.OUT_OF_STOCK && !isOutOfStock()) {
            this.lifecycle = ProductLifecycle.ACTIVE;
        }
    }
}