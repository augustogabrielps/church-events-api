package com.serve.controller;

import com.serve.domain.Event;
import com.serve.security.JwtService;
import com.serve.service.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.serve.domain.EventStatus.CANCELLED;
import static com.serve.domain.EventStatus.CLOSED;
import static com.serve.domain.EventStatus.OPEN;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
@AutoConfigureMockMvc(addFilters = false)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldOpenEvent() throws Exception {
        UUID eventId = UUID.randomUUID();
        when(eventService.openEvent(eventId)).thenReturn(createEvent(eventId, OPEN));

        mockMvc.perform(patch("/events/{id}/open", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventId.toString()))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void shouldCloseEvent() throws Exception {
        UUID eventId = UUID.randomUUID();
        when(eventService.closeEvent(eventId)).thenReturn(createEvent(eventId, CLOSED));

        mockMvc.perform(patch("/events/{id}/close", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventId.toString()))
                .andExpect(jsonPath("$.status").value("CLOSED"));
    }

    @Test
    void shouldCancelEvent() throws Exception {
        UUID eventId = UUID.randomUUID();
        when(eventService.cancelEvent(eventId)).thenReturn(createEvent(eventId, CANCELLED));

        mockMvc.perform(patch("/events/{id}/cancel", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventId.toString()))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    private Event createEvent(UUID eventId, com.serve.domain.EventStatus status) {
        Event event = new Event();
        event.setId(eventId);
        event.setTitle("Test Event");
        event.setStatus(status);

        return event;
    }
}
