package com.serve.service;

import com.serve.domain.Event;
import com.serve.domain.EventStatus;
import com.serve.repository.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional
    public Event createEvent(Event event) {
        if (event.getStatus() == null) {
            event.setStatus(EventStatus.DRAFT);
        }

        return eventRepository.save(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Transactional
    public Event openEvent(UUID eventId) {
        return updateStatus(eventId, EventStatus.OPEN);
    }

    @Transactional
    public Event closeEvent(UUID eventId) {
        return updateStatus(eventId, EventStatus.CLOSED);
    }

    @Transactional
    public Event cancelEvent(UUID eventId) {
        return updateStatus(eventId, EventStatus.CANCELLED);
    }

    private Event updateStatus(UUID eventId, EventStatus status) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        event.setStatus(status);

        return eventRepository.save(event);
    }
}
