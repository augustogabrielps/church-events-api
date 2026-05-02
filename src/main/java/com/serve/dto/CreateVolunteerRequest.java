package com.serve.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateVolunteerRequest(
        @NotBlank String name,
        @NotBlank @Email String email
) {
}
