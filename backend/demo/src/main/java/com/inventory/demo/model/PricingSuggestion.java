package com.inventory.demo.model;

import com.inventory.demo.enums.ChangeDirection;
import com.inventory.demo.enums.SuggestionStatus;
import com.inventory.demo.enums.TriggerReason;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "pricing_suggestions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PricingSuggestion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(name = "current_price", nullable = false)
    private BigDecimal currentPrice;
    
    @Column(name = "recommended_price", nullable = false)
    private BigDecimal recommendedPrice;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "change_direction", nullable = false)
    private ChangeDirection changeDirection;
    
    @Column(nullable = false)
    private Double confidence; // 0.0 - 1.0
    
    @Column(columnDefinition = "TEXT")
    private String reasoning;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SuggestionStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_reason", nullable = false)
    private TriggerReason triggerReason;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        updatedAt = java.time.LocalDateTime.now();
        if (status == null) {
            status = SuggestionStatus.PENDING;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }
}