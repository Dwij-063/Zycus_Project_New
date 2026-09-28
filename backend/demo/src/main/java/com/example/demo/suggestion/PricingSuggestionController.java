package com.example.demo.suggestion;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pricing-suggestions")
@CrossOrigin(origins = "http://localhost:5173")
public class PricingSuggestionController {

    private final PricingSuggestionRepository pricingRepository;

    public PricingSuggestionController(
            PricingSuggestionRepository pricingRepository) {
        this.pricingRepository = pricingRepository;
    }

    @GetMapping
    public List<PricingSuggestion> getAllSuggestions() {
        return pricingRepository.findAll();
    }
}