package com.serve.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record FinancialSummaryResponse(
        UUID eventId,
        BigDecimal totalSalesAmount,
        BigDecimal totalPaidAmount,
        BigDecimal totalPendingAmount,
        int totalTicketsSold,
        int remainingTickets
) {
}
