package com.serve.service;

import com.serve.domain.Event;
import com.serve.domain.EventDate;
import com.serve.repository.EventDateRepository;
import com.serve.repository.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class EventDateService {

    private final EventRepository eventRepository;
    private final EventDateRepository eventDateRepository;

    public EventDateService(EventRepository eventRepository, EventDateRepository eventDateRepository) {
        this.eventRepository = eventRepository;
        this.eventDateRepository = eventDateRepository;
    }

    public EventDate createEventDate(UUID eventId, EventDate eventDate) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        eventDate.setEvent(event);

        return eventDateRepository.save(eventDate);
    }
}
