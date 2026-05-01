package com.serve.service;

import com.serve.domain.Event;
import com.serve.domain.TicketType;
import com.serve.dto.TicketTypeResponse;
import com.serve.repository.EventRepository;
import com.serve.repository.TicketSaleRepository;
import com.serve.repository.TicketTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TicketTypeService {

    private final EventRepository eventRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketSaleRepository ticketSaleRepository;

    public TicketTypeService(
            EventRepository eventRepository,
            TicketTypeRepository ticketTypeRepository,
            TicketSaleRepository ticketSaleRepository
    ) {
        this.eventRepository = eventRepository;
        this.ticketTypeRepository = ticketTypeRepository;
        this.ticketSaleRepository = ticketSaleRepository;
    }

    @Transactional
    public TicketType createTicketType(UUID eventId, TicketType ticketType) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        ticketType.setEvent(event);

        return ticketTypeRepository.save(ticketType);
    }

    public List<TicketType> getTicketTypesByEventId(UUID eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Event not found");
        }

        return ticketTypeRepository.findByEvent_Id(eventId);
    }

    public List<TicketTypeResponse> getTicketTypeResponsesByEventId(UUID eventId) {
        return getTicketTypesByEventId(eventId).stream()
                .map(ticketType -> TicketTypeResponse.from(
                        ticketType,
                        ticketSaleRepository.sumQuantityByTicketTypeId(ticketType.getId())
                ))
                .toList();
    }
}
