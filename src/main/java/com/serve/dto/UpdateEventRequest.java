package com.serve.dto;

import jakarta.validation.constraints.Pattern;

public record UpdateEventRequest(
        @Pattern(regexp = ".*\\S.*", message = "must not be blank")
        String title,
        String description,
        String location
) {
}
