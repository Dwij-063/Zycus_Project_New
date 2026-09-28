package com.example.demo.suggestion;

import com.example.demo.entity.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SuggestionApprovalService {

    private final PricingSuggestionRepository pricingRepository;
    private final ReorderSuggestionRepository reorderRepository;

    public SuggestionApprovalService(
            PricingSuggestionRepository pricingRepository,
            ReorderSuggestionRepository reorderRepository) {

        this.pricingRepository = pricingRepository;
        this.reorderRepository = reorderRepository;
    }

    @Transactional
    public PricingSuggestion updatePricingStatus(
            Long id,
            SuggestionStatus status) {

        PricingSuggestion suggestion =
                pricingRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Pricing suggestion not found"));

        if (suggestion.getStatus() != SuggestionStatus.PENDING) {
            throw new RuntimeException(
                    "Only pending suggestions can be updated");
        }

        if (status == SuggestionStatus.ACCEPTED) {

            Product product = suggestion.getProduct();

            product.setCurrentPrice(
                    suggestion.getRecommendedPrice()
            );
        }

        suggestion.setStatus(status);

        return pricingRepository.save(suggestion);
    }

    @Transactional
    public ReorderSuggestion updateReorderStatus(
            Long id,
            SuggestionStatus status) {

        ReorderSuggestion suggestion =
                reorderRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Reorder suggestion not found"));

        if (suggestion.getStatus() != SuggestionStatus.PENDING) {
            throw new RuntimeException(
                    "Only pending suggestions can be updated");
        }

        if (status == SuggestionStatus.ACCEPTED) {

            Product product = suggestion.getProduct();

            int newStock =
                    product.getStockLevel()
                            + suggestion.getRecommendedQuantity();

            product.setStockLevel(newStock);

            if (newStock > 0) {
                product.setStatus(
                        com.example.demo.entity.ProductStatus.ACTIVE
                );
            }
        }

        suggestion.setStatus(status);

        return reorderRepository.save(suggestion);
    }
}