package com.example.demo.ai;

import com.example.demo.entity.Product;
import com.example.demo.suggestion.PricingDirection;
import com.example.demo.suggestion.SuggestionTrigger;
import org.springframework.stereotype.Service;

@Service
public class RuleBasedCommerceAdvisor implements CommerceAdvisor {

    @Override
    public PricingRecommendation recommendPricing(
            Product product,
            SuggestionTrigger trigger) {

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

        recommendedPrice =
                Math.round(recommendedPrice * 100.0) / 100.0;

        return new PricingRecommendation(
                recommendedPrice,
                direction,
                0.85,
                reasoning
        );
    }

    @Override
    public ReorderRecommendation recommendReorder(
            Product product,
            SuggestionTrigger trigger) {

        int recommendedQuantity =
                (product.getReorderThreshold() * 3)
                        - product.getStockLevel();

        recommendedQuantity =
                Math.max(1, recommendedQuantity);

        String reasoning =
                "Current inventory is below the reorder threshold. "
                + "The recommendation restores approximately "
                + "three threshold cycles of inventory.";

        return new ReorderRecommendation(
                recommendedQuantity,
                7,
                0.85,
                reasoning
        );
    }

    @Override
public AIRecommendationResponse recommendBoth(
        Product product,
        SuggestionTrigger trigger) {

    return new AIRecommendationResponse(
            recommendPricing(product, trigger),
            recommendReorder(product, trigger)
    );
}

}