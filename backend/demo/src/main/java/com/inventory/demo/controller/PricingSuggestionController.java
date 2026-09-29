package com.inventory.demo.controller;

import com.inventory.demo.model.PricingSuggestion;
import com.inventory.demo.service.PricingSuggestionService;
import com.inventory.demo.enums.SuggestionStatus;
import com.inventory.demo.enums.TriggerReason;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pricing-suggestions")
@CrossOrigin(origins = "*")
public class PricingSuggestionController {
    
    @Autowired
    private PricingSuggestionService pricingSuggestionService;
    
    @PostMapping("/for-product/{productId}")
    public ResponseEntity<PricingSuggestion> createPricingSuggestion(
            @PathVariable Long productId,
            @RequestParam TriggerReason triggerReason) {
        try {
            PricingSuggestion suggestion = pricingSuggestionService.createPricingSuggestion(productId, triggerReason);
            return ResponseEntity.ok(suggestion);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<PricingSuggestion>> getAllPricingSuggestions() {
        List<PricingSuggestion> suggestions = pricingSuggestionService.getAllPricingSuggestions();
        return ResponseEntity.ok(suggestions);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PricingSuggestion> getPricingSuggestionById(@PathVariable Long id) {
        Optional<PricingSuggestion> suggestion = pricingSuggestionService.getPricingSuggestionById(id);
        if (suggestion.isPresent()) {
            return ResponseEntity.ok(suggestion.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/by-status")
    public ResponseEntity<List<PricingSuggestion>> getPricingSuggestionsByStatus(
            @RequestParam SuggestionStatus status) {
        List<PricingSuggestion> suggestions = pricingSuggestionService.getPricingSuggestionsByStatus(status);
        return ResponseEntity.ok(suggestions);
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<PricingSuggestion> updatePricingSuggestionStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest request) {
        try {
            PricingSuggestion updatedSuggestion = pricingSuggestionService.updatePricingSuggestionStatus(
                id, request.getStatus());
            return ResponseEntity.ok(updatedSuggestion);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Helper class for status update requests
    public static class StatusUpdateRequest {
        private SuggestionStatus status;
        
        public SuggestionStatus getStatus() {
            return status;
        }
        
        public void setStatus(SuggestionStatus status) {
            this.status = status;
        }
    }
}