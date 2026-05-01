package com.serve.controller;

import com.serve.domain.EventDate;
import com.serve.dto.CreateEventDateRequest;
import com.serve.dto.EventDateResponse;
import com.serve.service.EventDateService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/events/{eventId}/dates")
public class EventDateController {

    private final EventDateService eventDateService;

    public EventDateController(EventDateService eventDateService) {
        this.eventDateService = eventDateService;
    }

    @PostMapping
    public ResponseEntity<EventDateResponse> createEventDate(
            @PathVariable UUID eventId,
            @Valid @RequestBody CreateEventDateRequest request
    ) {
        EventDate eventDate = new EventDate();
        eventDate.setEventDate(request.eventDate());
        eventDate.setStartTime(request.startTime());
        eventDate.setEndTime(request.endTime());

        EventDate createdEventDate = eventDateService.createEventDate(eventId, eventDate);

        return ResponseEntity
                .created(URI.create("/events/" + eventId + "/dates/" + createdEventDate.getId()))
                .body(EventDateResponse.from(createdEventDate));
    }
}
