package com.example.demo.suggestion;

import com.example.demo.ai.AIRecommendationResponse;
import com.example.demo.ai.CommerceAdvisor;
import com.example.demo.ai.PricingRecommendation;
import com.example.demo.ai.ReorderRecommendation;
import com.example.demo.entity.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SuggestionService {

    private final PricingSuggestionRepository pricingRepository;
    private final ReorderSuggestionRepository reorderRepository;
    private final CommerceAdvisor commerceAdvisor;

    @Value("${stockpulse.demand-spike.multiplier:1.5}")
    private double demandSpikeMultiplier;

    public SuggestionService(
            PricingSuggestionRepository pricingRepository,
            ReorderSuggestionRepository reorderRepository,
            CommerceAdvisor commerceAdvisor) {

        this.pricingRepository = pricingRepository;
        this.reorderRepository = reorderRepository;
        this.commerceAdvisor = commerceAdvisor;
    }

    @Transactional
    public void createSuggestions(
            Product product,
            SuggestionTrigger trigger) {

        /*
         * One unified AI/Rule-Based advisor call.
         * Returns both pricing and reorder recommendations.
         */
        AIRecommendationResponse recommendations =
                commerceAdvisor.recommendBoth(
                        product,
                        trigger
                );

        if (recommendations == null) {
            return;
        }

        /*
         * Create pricing suggestion only if
         * there is no existing pending suggestion.
         */
        createPricingSuggestion(
                product,
                trigger,
                recommendations.getPricing()
        );

        /*
         * Create reorder suggestion only if
         * there is no existing pending suggestion.
         */
        createReorderSuggestion(
                product,
                trigger,
                recommendations.getReorder()
        );
    }

    private void createPricingSuggestion(
            Product product,
            SuggestionTrigger trigger,
            PricingRecommendation recommendation) {

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

        if (recommendation == null) {
            return;
        }

        PricingSuggestion suggestion =
                new PricingSuggestion();

        suggestion.setProduct(product);

        suggestion.setCurrentPrice(
                product.getCurrentPrice()
        );

        suggestion.setRecommendedPrice(
                recommendation.getRecommendedPrice()
        );

        suggestion.setDirection(
                recommendation.getDirection()
        );

        suggestion.setConfidence(
                recommendation.getConfidence()
        );

        suggestion.setReasoning(
                recommendation.getReasoning()
        );

        suggestion.setStatus(
                SuggestionStatus.PENDING
        );

        suggestion.setTriggerReason(
                trigger
        );

        /*
         * The strategy will later be populated
         * dynamically as AI or RULE_BASED.
         */
        suggestion.setStrategy(
                "AI"
        );

        pricingRepository.save(suggestion);
    }

    private void createReorderSuggestion(
            Product product,
            SuggestionTrigger trigger,
            ReorderRecommendation recommendation) {

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

        if (recommendation == null) {
            return;
        }

        ReorderSuggestion suggestion =
                new ReorderSuggestion();

        suggestion.setProduct(product);

        suggestion.setCurrentStock(
                product.getStockLevel()
        );

        suggestion.setRecommendedQuantity(
                recommendation.getRecommendedQuantity()
        );

        suggestion.setLeadTimeDays(
                recommendation.getLeadTimeDays()
        );

        suggestion.setConfidence(
                recommendation.getConfidence()
        );

        suggestion.setReasoning(
                recommendation.getReasoning()
        );

        suggestion.setStatus(
                SuggestionStatus.PENDING
        );

        suggestion.setTriggerReason(
                trigger
        );

        /*
         * The strategy will later be populated
         * dynamically as AI or RULE_BASED.
         */
        suggestion.setStrategy(
                "AI"
        );

        reorderRepository.save(suggestion);
    }

    @Transactional
    public void checkDemandSpike(
            Product product,
            Double categoryAverage) {

        if (categoryAverage == null || categoryAverage <= 0) {
            return;
        }

        double spikeThreshold =
                categoryAverage * demandSpikeMultiplier;

        if (product.getDemandVelocity() > spikeThreshold) {

            createSuggestions(
                    product,
                    SuggestionTrigger.DEMAND_SPIKE
            );
        }
    }
}