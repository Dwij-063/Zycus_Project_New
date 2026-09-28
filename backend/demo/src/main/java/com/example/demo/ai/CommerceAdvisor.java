package com.example.demo.ai;

import com.example.demo.entity.Product;
import com.example.demo.suggestion.SuggestionTrigger;

public interface CommerceAdvisor {

    PricingRecommendation recommendPricing(
            Product product,
            SuggestionTrigger trigger
    );

    ReorderRecommendation recommendReorder(
            Product product,
            SuggestionTrigger trigger
    );

    AIRecommendationResponse recommendBoth(
            Product product,
            SuggestionTrigger trigger
    );
}