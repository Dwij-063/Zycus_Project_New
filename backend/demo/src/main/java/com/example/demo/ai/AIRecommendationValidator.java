package com.example.demo.ai;

import com.example.demo.entity.Product;
import com.example.demo.suggestion.PricingDirection;
import org.springframework.stereotype.Component;

@Component
public class AIRecommendationValidator {

    public boolean isValid(
            AIRecommendationResponse response,
            Product product) {

        if (response == null) {
            return false;
        }

        if (response.getPricing() == null) {
            return false;
        }

        if (response.getReorder() == null) {
            return false;
        }

        return isValidPricing(
                    response.getPricing(),
                    product)
                && isValidReorder(
                    response.getReorder());
    }

    private boolean isValidPricing(
            PricingRecommendation recommendation,
            Product product) {

        if (recommendation.getRecommendedPrice() == null) {
            return false;
        }

        if (recommendation.getRecommendedPrice() <= 0) {
            return false;
        }

        if (recommendation.getDirection() == null) {
            return false;
        }

        if (recommendation.getConfidence() == null) {
            return false;
        }

        if (recommendation.getConfidence() < 0
                || recommendation.getConfidence() > 1) {
            return false;
        }

        if (recommendation.getReasoning() == null
                || recommendation.getReasoning().isBlank()) {
            return false;
        }

        return true;
    }

    private boolean isValidReorder(
            ReorderRecommendation recommendation) {

        if (recommendation.getRecommendedQuantity() == null) {
            return false;
        }

        if (recommendation.getRecommendedQuantity() < 1) {
            return false;
        }

        if (recommendation.getLeadTimeDays() == null) {
            return false;
        }

        if (recommendation.getLeadTimeDays() < 0) {
            return false;
        }

        if (recommendation.getConfidence() == null) {
            return false;
        }

        if (recommendation.getConfidence() < 0
                || recommendation.getConfidence() > 1) {
            return false;
        }

        if (recommendation.getReasoning() == null
                || recommendation.getReasoning().isBlank()) {
            return false;
        }

        return true;
    }
}