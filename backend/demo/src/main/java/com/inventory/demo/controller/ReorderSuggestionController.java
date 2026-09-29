package com.inventory.demo.controller;

import com.inventory.demo.model.ReorderSuggestion;
import com.inventory.demo.service.ReorderSuggestionService;
import com.inventory.demo.enums.SuggestionStatus;
import com.inventory.demo.enums.TriggerReason;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reorder-suggestions")
@CrossOrigin(origins = "*")
public class ReorderSuggestionController {
    
    @Autowired
    private ReorderSuggestionService reorderSuggestionService;
    
    @PostMapping("/for-product/{productId}")
    public ResponseEntity<ReorderSuggestion> createReorderSuggestion(
            @PathVariable Long productId,
            @RequestParam TriggerReason triggerReason) {
        try {
            ReorderSuggestion suggestion = reorderSuggestionService.createReorderSuggestion(productId, triggerReason);
            return ResponseEntity.ok(suggestion);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<ReorderSuggestion>> getAllReorderSuggestions() {
        List<ReorderSuggestion> suggestions = reorderSuggestionService.getAllReorderSuggestions();
        return ResponseEntity.ok(suggestions);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ReorderSuggestion> getReorderSuggestionById(@PathVariable Long id) {
        Optional<ReorderSuggestion> suggestion = reorderSuggestionService.getReorderSuggestionById(id);
        if (suggestion.isPresent()) {
            return ResponseEntity.ok(suggestion.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/by-status")
    public ResponseEntity<List<ReorderSuggestion>> getReorderSuggestionsByStatus(
            @RequestParam SuggestionStatus status) {
        List<ReorderSuggestion> suggestions = reorderSuggestionService.getReorderSuggestionsByStatus(status);
        return ResponseEntity.ok(suggestions);
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<ReorderSuggestion> updateReorderSuggestionStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest request) {
        try {
            ReorderSuggestion updatedSuggestion = reorderSuggestionService.updateReorderSuggestionStatus(
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