package com.serve.dto;

import com.serve.domain.EventStatus;
import jakarta.validation.constraints.NotBlank;

public record CreateEventRequest(
        @NotBlank String title,
        String description,
        String location,
        EventStatus status
) {
}
