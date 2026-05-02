package com.serve.dto;

import com.serve.domain.VolunteerRole;

import java.util.List;
import java.util.UUID;

public record VolunteerRoleResponse(
        UUID id,
        UUID eventId,
        String name,
        String description,
        Integer requiredPeople,
        List<VolunteerResponse> volunteers
) {

    public static VolunteerRoleResponse from(VolunteerRole role) {
        return new VolunteerRoleResponse(
                role.getId(),
                role.getEvent().getId(),
                role.getName(),
                role.getDescription(),
                role.getRequiredPeople(),
                role.getVolunteers().stream()
                        .map(VolunteerResponse::from)
                        .toList()
        );
    }
}
