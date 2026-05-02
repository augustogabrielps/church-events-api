package com.serve.controller;

import com.serve.domain.Event;
import com.serve.dto.CreateEventRequest;
import com.serve.dto.EventResponse;
import com.serve.dto.UpdateEventRequest;
import com.serve.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody CreateEventRequest request) {
        Event event = new Event();
        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setLocation(request.location());
        event.setStatus(request.status());

        Event createdEvent = eventService.createEvent(event);

        return ResponseEntity
                .created(URI.create("/events/" + createdEvent.getId()))
                .body(EventResponse.from(createdEvent));
    }

    @GetMapping
    public List<EventResponse> getAllEvents() {
        return eventService.getAllEvents().stream()
                .map(EventResponse::from)
                .toList();
    }

    @PutMapping("/{id}")
    public EventResponse updateEvent(@PathVariable UUID id, @Valid @RequestBody UpdateEventRequest request) {
        return EventResponse.from(eventService.updateEvent(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/open")
    public EventResponse openEvent(@PathVariable UUID id) {
        return EventResponse.from(eventService.openEvent(id));
    }

    @PatchMapping("/{id}/close")
    public EventResponse closeEvent(@PathVariable UUID id) {
        return EventResponse.from(eventService.closeEvent(id));
    }

    @PatchMapping("/{id}/cancel")
    public EventResponse cancelEvent(@PathVariable UUID id) {
        return EventResponse.from(eventService.cancelEvent(id));
    }
}
