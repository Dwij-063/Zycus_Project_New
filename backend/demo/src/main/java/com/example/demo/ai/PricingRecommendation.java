package com.example.demo.ai;

import com.example.demo.suggestion.PricingDirection;

public class PricingRecommendation {

    private Double recommendedPrice;
    private PricingDirection direction;
    private Double confidence;
    private String reasoning;

    public PricingRecommendation() {
    }

    public PricingRecommendation(
            Double recommendedPrice,
            PricingDirection direction,
            Double confidence,
            String reasoning) {

        this.recommendedPrice = recommendedPrice;
        this.direction = direction;
        this.confidence = confidence;
        this.reasoning = reasoning;
    }

    public Double getRecommendedPrice() {
        return recommendedPrice;
    }

    public void setRecommendedPrice(Double recommendedPrice) {
        this.recommendedPrice = recommendedPrice;
    }

    public PricingDirection getDirection() {
        return direction;
    }

    public void setDirection(PricingDirection direction) {
        this.direction = direction;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public String getReasoning() {
        return reasoning;
    }

    public void setReasoning(String reasoning) {
        this.reasoning = reasoning;
    }
}