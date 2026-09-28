package com.example.demo.suggestion;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reorder-suggestions")
@CrossOrigin(origins = "http://localhost:5173")
public class ReorderSuggestionController {

    private final ReorderSuggestionRepository reorderRepository;
    private final SuggestionApprovalService approvalService;

    public ReorderSuggestionController(
            ReorderSuggestionRepository reorderRepository,
            SuggestionApprovalService approvalService) {

        this.reorderRepository = reorderRepository;
        this.approvalService = approvalService;
    }

    @GetMapping
    public List<ReorderSuggestion> getAllSuggestions() {
        return reorderRepository.findAll();
    }

    @PatchMapping("/{id}")
    public ReorderSuggestion updateStatus(
            @PathVariable Long id,
            @RequestParam SuggestionStatus status) {

        return approvalService.updateReorderStatus(id, status);
    }
}