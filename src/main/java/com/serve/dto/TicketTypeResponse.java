package com.serve.dto;

import com.serve.domain.TicketType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TicketTypeResponse(
        UUID id,
        UUID eventId,
        String name,
        String description,
        BigDecimal price,
        Integer availableQuantity,
        Integer remainingQuantity,
        LocalDateTime createdAt
) {

    public static TicketTypeResponse from(TicketType ticketType) {
        return from(ticketType, 0);
    }

    public static TicketTypeResponse from(TicketType ticketType, int totalSold) {
        return new TicketTypeResponse(
                ticketType.getId(),
                ticketType.getEvent().getId(),
                ticketType.getName(),
                ticketType.getDescription(),
                ticketType.getPrice(),
                ticketType.getAvailableQuantity(),
                ticketType.getAvailableQuantity() - totalSold,
                ticketType.getCreatedAt()
        );
    }
}
