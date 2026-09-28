package com.example.demo.suggestion;

import com.example.demo.entity.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "pricing_suggestions")
public class PricingSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull
    private Double currentPrice;

    @NotNull
    private Double recommendedPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PricingDirection direction;

    @DecimalMin("0.0")
    @DecimalMax("1.0")
    @Column(nullable = false)
    private Double confidence;

    @Column(length = 1000)
    private String reasoning;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SuggestionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SuggestionTrigger triggerReason;

    @Column(nullable = false)
    private String strategy;

    public PricingSuggestion() {
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
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

    public SuggestionStatus getStatus() {
        return status;
    }

    public void setStatus(SuggestionStatus status) {
        this.status = status;
    }

    public SuggestionTrigger getTriggerReason() {
        return triggerReason;
    }

    public void setTriggerReason(SuggestionTrigger triggerReason) {
        this.triggerReason = triggerReason;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }
}