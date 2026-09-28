package com.example.demo.ai;

public class ReorderRecommendation {

    private Integer recommendedQuantity;
    private Integer leadTimeDays;
    private Double confidence;
    private String reasoning;

    public ReorderRecommendation() {
    }

    public ReorderRecommendation(
            Integer recommendedQuantity,
            Integer leadTimeDays,
            Double confidence,
            String reasoning) {

        this.recommendedQuantity = recommendedQuantity;
        this.leadTimeDays = leadTimeDays;
        this.confidence = confidence;
        this.reasoning = reasoning;
    }

    public Integer getRecommendedQuantity() {
        return recommendedQuantity;
    }

    public void setRecommendedQuantity(Integer recommendedQuantity) {
        this.recommendedQuantity = recommendedQuantity;
    }

    public Integer getLeadTimeDays() {
        return leadTimeDays;
    }

    public void setLeadTimeDays(Integer leadTimeDays) {
        this.leadTimeDays = leadTimeDays;
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