package com.serve.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateVolunteerSignupRequest(
        @NotNull UUID userId,
        @NotNull UUID roleId,
        @NotNull UUID eventDateId
) {
}
