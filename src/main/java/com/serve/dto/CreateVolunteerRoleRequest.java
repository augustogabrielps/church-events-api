package com.serve.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateVolunteerRoleRequest(
        @NotBlank String name,
        String description,
        @Positive Integer requiredPeople
) {
}
