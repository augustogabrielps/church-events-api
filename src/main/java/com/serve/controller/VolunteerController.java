package com.serve.controller;

import com.serve.domain.Volunteer;
import com.serve.dto.CreateVolunteerRequest;
import com.serve.dto.VolunteerResponse;
import com.serve.service.VolunteerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/volunteers")
public class VolunteerController {

    private final VolunteerService volunteerService;

    public VolunteerController(VolunteerService volunteerService) {
        this.volunteerService = volunteerService;
    }

    @PostMapping
    public ResponseEntity<VolunteerResponse> createVolunteer(@Valid @RequestBody CreateVolunteerRequest request) {
        Volunteer volunteer = new Volunteer();
        volunteer.setName(request.name());
        volunteer.setEmail(request.email());

        Volunteer createdVolunteer = volunteerService.createVolunteer(volunteer);

        return ResponseEntity
                .created(URI.create("/volunteers/" + createdVolunteer.getId()))
                .body(VolunteerResponse.from(createdVolunteer));
    }

    @GetMapping
    public List<VolunteerResponse> getAllVolunteers() {
        return volunteerService.getAllVolunteers().stream()
                .map(VolunteerResponse::from)
                .toList();
    }
}
