package com.serve.controller;

import com.serve.domain.VolunteerSignup;
import com.serve.dto.CreateVolunteerSignupRequest;
import com.serve.dto.SignupResponse;
import com.serve.service.VolunteerSignupService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
public class VolunteerSignupController {

    private final VolunteerSignupService signupService;

    public VolunteerSignupController(VolunteerSignupService signupService) {
        this.signupService = signupService;
    }

    @PostMapping("/signups")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody CreateVolunteerSignupRequest request) {
        VolunteerSignup signup = signupService.signup(
                request.userId(),
                request.roleId(),
                request.eventDateId()
        );

        return ResponseEntity
                .created(URI.create("/signups/" + signup.getId()))
                .body(SignupResponse.from(
                        signup,
                        signupService.getRemainingSlots(request.roleId(), request.eventDateId())
                ));
    }

    @GetMapping("/events/{eventId}/signups")
    public List<SignupResponse> getEventSignups(@PathVariable UUID eventId) {
        return signupService.getSignupsByEventId(eventId).stream()
                .map(signup -> SignupResponse.from(
                        signup,
                        signupService.getRemainingSlots(
                                signup.getRole().getId(),
                                signup.getEventDate().getId()
                        )
                ))
                .toList();
    }
}
