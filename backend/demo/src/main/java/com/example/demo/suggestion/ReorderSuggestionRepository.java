package com.example.demo.suggestion;

import com.example.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReorderSuggestionRepository
        extends JpaRepository<ReorderSuggestion, Long> {

    List<ReorderSuggestion> findByProduct(Product product);

    List<ReorderSuggestion> findByStatus(SuggestionStatus status);

    boolean existsByProductAndTriggerReasonAndStatus(
            Product product,
            SuggestionTrigger triggerReason,
            SuggestionStatus status
    );
}