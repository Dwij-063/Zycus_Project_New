package com.example.demo.suggestion;

import com.example.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PricingSuggestionRepository
        extends JpaRepository<PricingSuggestion, Long> {

    List<PricingSuggestion> findByProduct(Product product);

    List<PricingSuggestion> findByStatus(SuggestionStatus status);

    boolean existsByProductAndTriggerReasonAndStatus(
            Product product,
            SuggestionTrigger triggerReason,
            SuggestionStatus status
    );
}