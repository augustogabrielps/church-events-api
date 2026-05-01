package com.serve.dto;

import com.serve.domain.TicketSale;
import com.serve.domain.TicketSaleStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TicketSaleResponse(
        UUID id,
        UUID eventId,
        UUID ticketTypeId,
        String ticketTypeName,
        UUID sellerId,
        String buyerName,
        Integer quantity,
        BigDecimal totalAmount,
        BigDecimal remainingAmount,
        TicketSaleStatus status,
        LocalDateTime createdAt
) {

    public static TicketSaleResponse from(TicketSale sale, BigDecimal remainingAmount) {
        return new TicketSaleResponse(
                sale.getId(),
                sale.getEvent().getId(),
                sale.getTicketType().getId(),
                sale.getTicketType().getName(),
                sale.getSeller().getId(),
                sale.getBuyerName(),
                sale.getQuantity(),
                sale.getTotalAmount(),
                remainingAmount,
                sale.getStatus(),
                sale.getCreatedAt()
        );
    }
}
