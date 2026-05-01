package com.serve.controller;

import com.serve.domain.Event;
import com.serve.domain.EventDate;
import com.serve.domain.SignupStatus;
import com.serve.domain.User;
import com.serve.domain.VolunteerRole;
import com.serve.domain.VolunteerSignup;
import com.serve.exception.BusinessRuleException;
import com.serve.exception.ResourceConflictException;
import com.serve.security.JwtService;
import com.serve.service.VolunteerSignupService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static com.serve.domain.EventStatus.OPEN;
import static com.serve.domain.UserRole.VOLUNTEER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VolunteerSignupController.class)
@AutoConfigureMockMvc(addFilters = false)
class VolunteerSignupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VolunteerSignupService signupService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldReturn201WhenSignupIsCreated() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        UUID eventDateId = UUID.randomUUID();
        VolunteerSignup signup = createSignup(userId, roleId, eventDateId);

        when(signupService.signup(userId, roleId, eventDateId)).thenReturn(signup);
        when(signupService.getRemainingSlots(roleId, eventDateId)).thenReturn(1);

        mockMvc.perform(post("/signups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": "%s",
                                  "roleId": "%s",
                                  "eventDateId": "%s"
                                }
                                """.formatted(userId, roleId, eventDateId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(signup.getId().toString()))
                .andExpect(jsonPath("$.remainingSlots").value(1));
    }

    @Test
    void shouldReturn400WhenInvalidRequest() throws Exception {
        mockMvc.perform(post("/signups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": null
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn409WhenDuplicateSignup() throws Exception {
        when(signupService.signup(any(), any(), any()))
                .thenThrow(new ResourceConflictException("User is already signed up for this role and date"));

        mockMvc.perform(post("/signups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestBody()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("User is already signed up for this role and date"));
    }

    @Test
    void shouldReturn400WhenBusinessRuleFails() throws Exception {
        when(signupService.signup(any(), any(), any()))
                .thenThrow(new BusinessRuleException("No more slots available for this role"));

        mockMvc.perform(post("/signups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestBody()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("No more slots available for this role"));
    }

    @Test
    void shouldReturn400WhenEventIsNotOpen() throws Exception {
        when(signupService.signup(any(), any(), any()))
                .thenThrow(new BusinessRuleException("Signups are not allowed for this event"));

        mockMvc.perform(post("/signups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestBody()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Signups are not allowed for this event"));
    }

    @Test
    void shouldReturn404WhenResourceNotFound() throws Exception {
        when(signupService.signup(any(), any(), any()))
                .thenThrow(new EntityNotFoundException("Role not found"));

        mockMvc.perform(post("/signups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestBody()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Role not found"));
    }

    private String validRequestBody() {
        return """
                {
                  "userId": "%s",
                  "roleId": "%s",
                  "eventDateId": "%s"
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
    }

    private VolunteerSignup createSignup(UUID userId, UUID roleId, UUID eventDateId) {
        Event event = new Event();
        event.setId(UUID.randomUUID());
        event.setTitle("Test Event");
        event.setStatus(OPEN);

        User user = new User();
        user.setId(userId);
        user.setName("Test Volunteer");
        user.setEmail("volunteer@email.com");
        user.setRole(VOLUNTEER);

        EventDate eventDate = new EventDate();
        eventDate.setId(eventDateId);
        eventDate.setEvent(event);
        eventDate.setEventDate(LocalDate.now());

        VolunteerRole role = new VolunteerRole();
        role.setId(roleId);
        role.setEvent(event);
        role.setName("Kitchen");
        role.setRequiredPeople(2);

        VolunteerSignup signup = new VolunteerSignup();
        signup.setId(UUID.randomUUID());
        signup.setUser(user);
        signup.setRole(role);
        signup.setEventDate(eventDate);
        signup.setStatus(SignupStatus.CONFIRMED);

        return signup;
    }
}
