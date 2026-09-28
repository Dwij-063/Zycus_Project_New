package com.example.demo.ai;

public class AIRecommendationResponse {

    private PricingRecommendation pricing;
    private ReorderRecommendation reorder;

    public AIRecommendationResponse() {
    }

    public AIRecommendationResponse(
            PricingRecommendation pricing,
            ReorderRecommendation reorder) {

        this.pricing = pricing;
        this.reorder = reorder;
    }

    public PricingRecommendation getPricing() {
        return pricing;
    }

    public void setPricing(PricingRecommendation pricing) {
        this.pricing = pricing;
    }

    public ReorderRecommendation getReorder() {
        return reorder;
    }

    public void setReorder(ReorderRecommendation reorder) {
        this.reorder = reorder;
    }
}