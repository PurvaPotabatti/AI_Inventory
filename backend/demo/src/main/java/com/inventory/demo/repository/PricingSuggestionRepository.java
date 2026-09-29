package com.inventory.demo.repository;

import com.inventory.demo.model.PricingSuggestion;
import com.inventory.demo.model.Product;
import com.inventory.demo.enums.SuggestionStatus;
import com.inventory.demo.enums.TriggerReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PricingSuggestionRepository extends JpaRepository<PricingSuggestion, Long> {
    List<PricingSuggestion> findByProduct(Product product);
    
    List<PricingSuggestion> findByStatus(SuggestionStatus status);
    
    List<PricingSuggestion> findByTriggerReason(TriggerReason triggerReason);
    
    @Query("SELECT ps FROM PricingSuggestion ps WHERE " +
           "(:status IS NULL OR ps.status = :status) AND " +
           "(:productId IS NULL OR ps.product.id = :productId)")
    List<PricingSuggestion> findByStatusAndProductId(
        @Param("status") SuggestionStatus status,
        @Param("productId") Long productId
    );
    
    @Query("SELECT ps FROM PricingSuggestion ps WHERE " +
           "ps.status = com.inventory.demo.enums.SuggestionStatus.PENDING AND " +
           "ps.product.id = :productId")
    List<PricingSuggestion> findByPendingStatusAndProductId(
        @Param("productId") Long productId
    );
}