
package com.example.demo.ai;

import com.example.demo.entity.Product;
import com.example.demo.suggestion.SuggestionTrigger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

@Service
@Primary
public class AICommerceAdvisor implements CommerceAdvisor {

    private final ObjectMapper objectMapper;
    private final WebClient webClient;
    private final AIRecommendationValidator validator;
    private final RuleBasedCommerceAdvisor ruleBasedAdvisor;

    @Value("${stockpulse.ai.api-key}")
    private String apiKey;

    @Value("${stockpulse.ai.model:gemini-1.5-flash}")
    private String model;

    public AICommerceAdvisor(
            WebClient.Builder webClientBuilder,
            AIRecommendationValidator validator,
            RuleBasedCommerceAdvisor ruleBasedAdvisor,
            ObjectMapper objectMapper) {

        this.webClient = webClientBuilder.build();
        this.validator = validator;
        this.ruleBasedAdvisor = ruleBasedAdvisor;
        this.objectMapper = objectMapper;
    }

    @Override
    public PricingRecommendation recommendPricing(
            Product product,
            SuggestionTrigger trigger) {

        AIRecommendationResponse response =
                recommendBoth(product, trigger);

        return response.getPricing();
    }

    @Override
    public ReorderRecommendation recommendReorder(
            Product product,
            SuggestionTrigger trigger) {

        AIRecommendationResponse response =
                recommendBoth(product, trigger);

        return response.getReorder();
    }

    @Override
    public AIRecommendationResponse recommendBoth(
            Product product,
            SuggestionTrigger trigger) {

        /*
         * If AI is disabled, directly use rule-based strategy.
         */
        if (!isAIEnabled()) {

            System.out.println(
                    "AI advisor is disabled. " +
                    "Using rule-based advisor."
            );

            return ruleBasedAdvisor.recommendBoth(
                    product,
                    trigger
            );
        }

        AIRecommendationResponse response =
                getAIRecommendation(product, trigger);

        /*
         * Validate the complete AI response before using it.
         */
        if (response != null
                && validator.isValid(response, product)) {

            System.out.println(
                    "Gemini recommendation successfully generated."
            );

            return response;
        }

        /*
         * Gemini failed, timed out, returned malformed JSON,
         * or produced an invalid recommendation.
         */
        System.out.println(
                "AI recommendation unavailable. " +
                "Using rule-based fallback."
        );

        return ruleBasedAdvisor.recommendBoth(
                product,
                trigger
        );
    }

    private AIRecommendationResponse getAIRecommendation(
            Product product,
            SuggestionTrigger trigger) {

        String prompt =
                buildPrompt(product, trigger);

        try {

            GeminiRequest request =
                    new GeminiRequest(
                            List.of(
                                    new GeminiRequest.Content(
                                            List.of(
                                                    new GeminiRequest.Part(
                                                            prompt
                                                    )
                                            )
                                    )
                            )
                    );

            GeminiResponse response =
                    webClient.post()
                            .uri(uriBuilder ->
                                    uriBuilder
                                            .scheme("https")
                                            .host(
                                                    "generativelanguage.googleapis.com"
                                            )
                                            .path(
                                                    "/v1beta/models/"
                                            )
                                            .path(model)
                                            .path(
                                                    ":generateContent"
                                            )
                                            .queryParam(
                                                    "key",
                                                    apiKey
                                            )
                                            .build()
                            )
                            .contentType(
                                    MediaType.APPLICATION_JSON
                            )
                            .bodyValue(request)
                            .retrieve()
                            .bodyToMono(
                                    GeminiResponse.class
                            )
                            .block(
                                    Duration.ofSeconds(30)
                            );

            /*
             * Validate basic Gemini response structure.
             */
            if (response == null
                    || response.getCandidates() == null
                    || response.getCandidates().isEmpty()) {

                System.out.println(
                        "Gemini returned no candidates."
                );

                return null;
            }

            GeminiResponse.Candidate candidate =
                    response.getCandidates().get(0);

            if (candidate == null
                    || candidate.getContent() == null
                    || candidate.getContent().getParts() == null
                    || candidate.getContent()
                            .getParts()
                            .isEmpty()) {

                System.out.println(
                        "Gemini response contained no usable content."
                );

                return null;
            }

            String json =
                    candidate
                            .getContent()
                            .getParts()
                            .get(0)
                            .getText();

            if (json == null || json.isBlank()) {

                System.out.println(
                        "Gemini returned an empty response."
                );

                return null;
            }

            return parseAIResponse(json);

        } catch (Exception exception) {

            System.out.println(
                    "Gemini request failed: "
                            + exception.getMessage()
            );

            return null;
        }
    }

    private boolean isAIEnabled() {

        return apiKey != null
                && !apiKey.isBlank()
                && !apiKey.equals("YOUR_ACTUAL_GEMINI_KEY")
                && !apiKey.equals("YOUR_NEW_REAL_GEMINI_KEY");
    }

    private String buildPrompt(
            Product product,
            SuggestionTrigger trigger) {

        String triggerInstructions;

        if (trigger == SuggestionTrigger.INVENTORY_LOW) {

            triggerInstructions = """
                    The product has reached a low-inventory condition.
                    Focus on preventing stockout while considering the
                    current price, reorder threshold, and demand velocity.
                    Recommend a commercially reasonable price adjustment
                    and reorder quantity.
                    """;

        } else if (trigger == SuggestionTrigger.DEMAND_SPIKE) {

            triggerInstructions = """
                    The product is experiencing a demand spike.
                    Focus on capturing increased demand while avoiding
                    excessive price changes and ensuring sufficient
                    inventory replenishment.
                    Recommend a commercially reasonable price adjustment
                    and reorder quantity.
                    """;

        } else {

            triggerInstructions = """
                    Analyze the current product state and provide a
                    balanced pricing and inventory recommendation.
                    """;
        }

        return """
                You are a commerce pricing and inventory advisor
                for an e-commerce merchandising team.

                Analyze the following product information.

                Product name: %s
                SKU: %s
                Category: %s
                Current price: %.2f
                Current stock: %d
                Reorder threshold: %d
                Demand velocity: %.2f
                Trigger: %s

                Trigger-specific instructions:
                %s

                Return ONLY valid JSON.
                Do not use markdown.
                Do not include ```json.
                Do not include any text outside the JSON object.

                Required JSON format:

                {
                  "pricing": {
                    "recommendedPrice": 0.0,
                    "direction": "INCREASE",
                    "confidence": 0.0,
                    "reasoning": "reason"
                  },
                  "reorder": {
                    "recommendedQuantity": 0,
                    "leadTimeDays": 0,
                    "confidence": 0.0,
                    "reasoning": "reason"
                  }
                }

                Requirements:
                - recommendedPrice must be greater than 0.
                - direction must be INCREASE, DECREASE, or HOLD.
                - pricing confidence must be between 0 and 1.
                - recommendedQuantity must be at least 1.
                - leadTimeDays must be 0 or greater.
                - reorder confidence must be between 0 and 1.
                - reasoning must explain the recommendation.
                - Base the recommendation on the supplied product data.
                - Do not invent unavailable product information.
                """.formatted(
                product.getName(),
                product.getSku(),
                product.getCategory(),
                product.getCurrentPrice(),
                product.getStockLevel(),
                product.getReorderThreshold(),
                product.getDemandVelocity(),
                trigger,
                triggerInstructions
        );
    }

    private AIRecommendationResponse parseAIResponse(
            String json) {

        try {

            json = json.trim();

            /*
             * Remove Markdown code fences if Gemini
             * returns them despite the prompt.
             */
            if (json.startsWith("```json")) {
                json = json.substring(7);
            }

            if (json.startsWith("```")) {
                json = json.substring(3);
            }

            if (json.endsWith("```")) {
                json = json.substring(
                        0,
                        json.length() - 3
                );
            }

            json = json.trim();

            return objectMapper.readValue(
                    json,
                    AIRecommendationResponse.class
            );

        } catch (Exception exception) {

            System.out.println(
                    "Failed to parse Gemini response: "
                            + exception.getMessage()
            );

            return null;

        }
    }
}
