package com.serve.dto;

import com.serve.domain.EventDate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record EventDateResponse(
        UUID id,
        UUID eventId,
        LocalDate eventDate,
        LocalTime startTime,
        LocalTime endTime
) {

    public static EventDateResponse from(EventDate eventDate) {
        return new EventDateResponse(
                eventDate.getId(),
                eventDate.getEvent().getId(),
                eventDate.getEventDate(),
                eventDate.getStartTime(),
                eventDate.getEndTime()
        );
    }
}
