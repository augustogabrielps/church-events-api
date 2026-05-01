package com.serve.dto;

import com.serve.domain.Event;
import com.serve.domain.EventStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventResponse(
        UUID id,
        String title,
        String description,
        String location,
        EventStatus status,
        LocalDateTime createdAt
) {

    public static EventResponse from(Event event) {
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getStatus(),
                event.getCreatedAt()
        );
    }
}
