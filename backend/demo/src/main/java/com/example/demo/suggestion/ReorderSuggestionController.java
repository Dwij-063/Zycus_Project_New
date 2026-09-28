package com.example.demo.suggestion;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reorder-suggestions")
@CrossOrigin(origins = "http://localhost:5173")
public class ReorderSuggestionController {

    private final ReorderSuggestionRepository reorderRepository;

    public ReorderSuggestionController(
            ReorderSuggestionRepository reorderRepository) {
        this.reorderRepository = reorderRepository;
    }

    @GetMapping
    public List<ReorderSuggestion> getAllSuggestions() {
        return reorderRepository.findAll();
    }
}