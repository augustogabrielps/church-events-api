package com.serve.controller;

import com.serve.domain.TicketType;
import com.serve.dto.CreateTicketTypeRequest;
import com.serve.dto.TicketTypeResponse;
import com.serve.service.TicketTypeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/events/{eventId}/ticket-types")
public class TicketTypeController {

    private final TicketTypeService ticketTypeService;

    public TicketTypeController(TicketTypeService ticketTypeService) {
        this.ticketTypeService = ticketTypeService;
    }

    @PostMapping
    public ResponseEntity<TicketTypeResponse> createTicketType(
            @PathVariable UUID eventId,
            @Valid @RequestBody CreateTicketTypeRequest request
    ) {
        TicketType ticketType = new TicketType();
        ticketType.setName(request.name());
        ticketType.setDescription(request.description());
        ticketType.setPrice(request.price());
        ticketType.setAvailableQuantity(request.availableQuantity());

        TicketType createdTicketType = ticketTypeService.createTicketType(eventId, ticketType);

        return ResponseEntity
                .created(URI.create("/events/" + eventId + "/ticket-types/" + createdTicketType.getId()))
                .body(TicketTypeResponse.from(createdTicketType));
    }

    @GetMapping
    public List<TicketTypeResponse> getTicketTypesByEvent(@PathVariable UUID eventId) {
        return ticketTypeService.getTicketTypeResponsesByEventId(eventId);
    }
}
