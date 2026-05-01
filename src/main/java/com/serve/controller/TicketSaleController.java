package com.serve.controller;

import com.serve.domain.TicketSale;
import com.serve.dto.CreateTicketSaleRequest;
import com.serve.dto.TicketSaleResponse;
import com.serve.service.TicketSaleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
public class TicketSaleController {

    private final TicketSaleService ticketSaleService;

    public TicketSaleController(TicketSaleService ticketSaleService) {
        this.ticketSaleService = ticketSaleService;
    }

    @PostMapping("/ticket-sales")
    public ResponseEntity<TicketSaleResponse> createTicketSale(@Valid @RequestBody CreateTicketSaleRequest request) {
        TicketSale sale = new TicketSale();
        sale.setBuyerName(request.buyerName());
        sale.setQuantity(request.quantity());
        sale.setStatus(request.status());

        TicketSale createdSale = ticketSaleService.createTicketSale(
                sale,
                request.eventId(),
                request.ticketTypeId(),
                request.sellerId()
        );

        return ResponseEntity
                .created(URI.create("/ticket-sales/" + createdSale.getId()))
                .body(TicketSaleResponse.from(createdSale, ticketSaleService.getRemainingAmount(createdSale.getId())));
    }

    @GetMapping("/events/{eventId}/ticket-sales")
    public List<TicketSaleResponse> getTicketSalesByEvent(@PathVariable UUID eventId) {
        return ticketSaleService.getTicketSalesByEventId(eventId).stream()
                .map(sale -> TicketSaleResponse.from(sale, ticketSaleService.getRemainingAmount(sale.getId())))
                .toList();
    }
}
