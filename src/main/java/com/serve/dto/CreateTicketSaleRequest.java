package com.serve.dto;

import com.serve.domain.TicketSaleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CreateTicketSaleRequest(
        @NotNull UUID eventId,
        @NotNull UUID ticketTypeId,
        @NotNull UUID sellerId,
        @NotBlank String buyerName,
        @NotNull @Positive Integer quantity,
        TicketSaleStatus status
) {
}
