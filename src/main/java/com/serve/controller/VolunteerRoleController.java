package com.serve.controller;

import com.serve.domain.VolunteerRole;
import com.serve.dto.CreateVolunteerRoleRequest;
import com.serve.dto.VolunteerRoleResponse;
import com.serve.service.VolunteerRoleService;
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
@RequestMapping("/events/{eventId}/roles")
public class VolunteerRoleController {

    private final VolunteerRoleService volunteerRoleService;

    public VolunteerRoleController(VolunteerRoleService volunteerRoleService) {
        this.volunteerRoleService = volunteerRoleService;
    }

    @PostMapping
    public ResponseEntity<VolunteerRoleResponse> createVolunteerRole(
            @PathVariable UUID eventId,
            @Valid @RequestBody CreateVolunteerRoleRequest request
    ) {
        VolunteerRole role = new VolunteerRole();
        role.setName(request.name());
        role.setDescription(request.description());
        role.setRequiredPeople(request.requiredPeople());

        VolunteerRole createdRole = volunteerRoleService.createVolunteerRole(eventId, role);

        return ResponseEntity
                .created(URI.create("/events/" + eventId + "/roles/" + createdRole.getId()))
                .body(VolunteerRoleResponse.from(createdRole));
    }
}
