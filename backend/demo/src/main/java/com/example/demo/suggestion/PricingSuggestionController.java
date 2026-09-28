package com.example.demo.suggestion;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pricing-suggestions")
@CrossOrigin(origins = "http://localhost:5173")
public class PricingSuggestionController {

    private final PricingSuggestionRepository pricingRepository;
    private final SuggestionApprovalService approvalService;

    public PricingSuggestionController(
            PricingSuggestionRepository pricingRepository,
            SuggestionApprovalService approvalService) {

        this.pricingRepository = pricingRepository;
        this.approvalService = approvalService;
    }

    @GetMapping
    public List<PricingSuggestion> getAllSuggestions() {
        return pricingRepository.findAll();
    }

    @PatchMapping("/{id}")
    public PricingSuggestion updateStatus(
            @PathVariable Long id,
            @RequestParam SuggestionStatus status) {

        return approvalService.updatePricingStatus(id, status);
    }
}