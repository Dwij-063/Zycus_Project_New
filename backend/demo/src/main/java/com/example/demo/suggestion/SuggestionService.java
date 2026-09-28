package com.example.demo.suggestion;

import com.example.demo.entity.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SuggestionService {

    private final PricingSuggestionRepository pricingRepository;
    private final ReorderSuggestionRepository reorderRepository;

    public SuggestionService(
            PricingSuggestionRepository pricingRepository,
            ReorderSuggestionRepository reorderRepository) {

        this.pricingRepository = pricingRepository;
        this.reorderRepository = reorderRepository;
    }

    @Transactional
    public void createSuggestions(
            Product product,
            SuggestionTrigger trigger) {

        createPricingSuggestion(product, trigger);
        createReorderSuggestion(product, trigger);
    }

    private void createPricingSuggestion(
            Product product,
            SuggestionTrigger trigger) {

        boolean alreadyExists =
                pricingRepository
                        .existsByProductAndTriggerReasonAndStatus(
                                product,
                                trigger,
                                SuggestionStatus.PENDING
                        );

        if (alreadyExists) {
            return;
        }

        double currentPrice = product.getCurrentPrice();
        double recommendedPrice;
        PricingDirection direction;
        String reasoning;

        if (trigger == SuggestionTrigger.INVENTORY_LOW) {

            recommendedPrice = currentPrice * 1.10;
            direction = PricingDirection.INCREASE;

            reasoning =
                    "Inventory is below the reorder threshold. "
                    + "A 10% price increase can help protect "
                    + "remaining inventory while replenishment is initiated.";

        } else if (trigger == SuggestionTrigger.DEMAND_SPIKE) {

            recommendedPrice = currentPrice * 1.05;
            direction = PricingDirection.INCREASE;

            reasoning =
                    "Demand velocity is significantly above the "
                    + "category average. A 5% price increase can "
                    + "help balance strong demand against available inventory.";

        } else {

            recommendedPrice = currentPrice;
            direction = PricingDirection.HOLD;

            reasoning =
                    "No significant inventory or demand signal "
                    + "requires a price adjustment.";
        }

        PricingSuggestion suggestion = new PricingSuggestion();

        suggestion.setProduct(product);
        suggestion.setCurrentPrice(currentPrice);
        suggestion.setRecommendedPrice(
                Math.round(recommendedPrice * 100.0) / 100.0
        );
        suggestion.setDirection(direction);
        suggestion.setConfidence(0.85);
        suggestion.setReasoning(reasoning);
        suggestion.setStatus(SuggestionStatus.PENDING);
        suggestion.setTriggerReason(trigger);
        suggestion.setStrategy("RULE_BASED");

        pricingRepository.save(suggestion);
    }

    private void createReorderSuggestion(
            Product product,
            SuggestionTrigger trigger) {

        boolean alreadyExists =
                reorderRepository
                        .existsByProductAndTriggerReasonAndStatus(
                                product,
                                trigger,
                                SuggestionStatus.PENDING
                        );

        if (alreadyExists) {
            return;
        }

        int recommendedQuantity =
                (product.getReorderThreshold() * 3)
                        - product.getStockLevel();

        recommendedQuantity =
                Math.max(1, recommendedQuantity);

        ReorderSuggestion suggestion =
                new ReorderSuggestion();

        suggestion.setProduct(product);
        suggestion.setCurrentStock(product.getStockLevel());
        suggestion.setRecommendedQuantity(recommendedQuantity);
        suggestion.setLeadTimeDays(7);
        suggestion.setConfidence(0.85);

        suggestion.setReasoning(
                "Current inventory is below the reorder threshold. "
                + "The recommendation restores approximately "
                + "three threshold cycles of inventory."
        );

        suggestion.setStatus(SuggestionStatus.PENDING);
        suggestion.setTriggerReason(trigger);
        suggestion.setStrategy("RULE_BASED");

        reorderRepository.save(suggestion);
    }
}