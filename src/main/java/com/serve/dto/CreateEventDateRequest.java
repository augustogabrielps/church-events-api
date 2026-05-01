package com.serve.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateEventDateRequest(
        @NotNull LocalDate eventDate,
        LocalTime startTime,
        LocalTime endTime
) {
}
