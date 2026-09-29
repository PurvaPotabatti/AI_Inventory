package com.inventory.demo.repository;

import com.inventory.demo.model.ReorderSuggestion;
import com.inventory.demo.model.Product;
import com.inventory.demo.enums.SuggestionStatus;
import com.inventory.demo.enums.TriggerReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReorderSuggestionRepository extends JpaRepository<ReorderSuggestion, Long> {
    List<ReorderSuggestion> findByProduct(Product product);
    
    List<ReorderSuggestion> findByStatus(SuggestionStatus status);
    
    List<ReorderSuggestion> findByTriggerReason(TriggerReason triggerReason);
    
    @Query("SELECT rs FROM ReorderSuggestion rs WHERE " +
           "(:status IS NULL OR rs.status = :status) AND " +
           "(:productId IS NULL OR rs.product.id = :productId)")
    List<ReorderSuggestion> findByStatusAndProductId(
        @Param("status") SuggestionStatus status,
        @Param("productId") Long productId
    );
    
    @Query("SELECT rs FROM ReorderSuggestion rs WHERE " +
           "rs.status = com.inventory.demo.enums.SuggestionStatus.PENDING AND " +
           "rs.product.id = :productId")
    List<ReorderSuggestion> findByPendingStatusAndProductId(
        @Param("productId") Long productId
    );
}