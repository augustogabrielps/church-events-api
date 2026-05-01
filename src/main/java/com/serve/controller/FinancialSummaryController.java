package com.serve.controller;

import com.serve.dto.FinancialSummaryResponse;
import com.serve.service.FinancialSummaryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class FinancialSummaryController {

    private final FinancialSummaryService financialSummaryService;

    public FinancialSummaryController(FinancialSummaryService financialSummaryService) {
        this.financialSummaryService = financialSummaryService;
    }

    @GetMapping("/events/{eventId}/financial-summary")
    public FinancialSummaryResponse getFinancialSummary(@PathVariable UUID eventId) {
        return financialSummaryService.getFinancialSummary(eventId);
    }
}
