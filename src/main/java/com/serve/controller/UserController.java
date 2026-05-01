package com.serve.controller;

import com.serve.domain.User;
import com.serve.dto.CreateUserRequest;
import com.serve.dto.SignupResponse;
import com.serve.dto.UserResponse;
import com.serve.service.UserService;
import com.serve.service.VolunteerSignupService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final VolunteerSignupService volunteerSignupService;

    public UserController(UserService userService, VolunteerSignupService volunteerSignupService) {
        this.userService = userService;
        this.volunteerSignupService = volunteerSignupService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setRole(request.role());

        User createdUser = userService.createUser(user);

        return ResponseEntity
                .created(URI.create("/users/" + createdUser.getId()))
                .body(UserResponse.from(createdUser));
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(UserResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable UUID id) {
        return UserResponse.from(userService.getUserById(id));
    }

    @GetMapping("/{id}/signups")
    public List<SignupResponse> getUserSignups(@PathVariable UUID id) {
        return volunteerSignupService.getSignupsByUserId(id).stream()
                .map(signup -> SignupResponse.from(
                        signup,
                        volunteerSignupService.getRemainingSlots(
                                signup.getRole().getId(),
                                signup.getEventDate().getId()
                        )
                ))
                .toList();
    }
}
