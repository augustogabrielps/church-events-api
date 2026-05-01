package com.serve.service;

import com.serve.domain.Event;
import com.serve.domain.TicketSale;
import com.serve.domain.TicketType;
import com.serve.domain.User;
import com.serve.dto.TicketTypeResponse;
import com.serve.exception.BusinessRuleException;
import com.serve.repository.EventRepository;
import com.serve.repository.TicketTypeRepository;
import com.serve.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static com.serve.domain.EventStatus.OPEN;
import static com.serve.domain.TicketSaleStatus.CANCELLED;
import static com.serve.domain.UserRole.VOLUNTEER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TicketStockServiceTest {

    @Autowired
    private TicketSaleService ticketSaleService;

    @Autowired
    private TicketTypeService ticketTypeService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TicketTypeRepository ticketTypeRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldAllowSaleWhenStockAvailable() {
        Event event = createEvent();
        TicketType ticketType = createTicketType(event, 10);
        User seller = createUser("stock-available@email.com");

        TicketSale sale = ticketSaleService.createTicketSale(
                createSale("Buyer", 4),
                event.getId(),
                ticketType.getId(),
                seller.getId()
        );

        assertNotNull(sale.getId());
        assertEquals(4, sale.getQuantity());
    }

    @Test
    void shouldRejectSaleWhenStockExceeded() {
        Event event = createEvent();
        TicketType ticketType = createTicketType(event, 5);
        User seller = createUser("stock-exceeded@email.com");

        ticketSaleService.createTicketSale(
                createSale("First Buyer", 4),
                event.getId(),
                ticketType.getId(),
                seller.getId()
        );

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> ticketSaleService.createTicketSale(
                        createSale("Second Buyer", 2),
                        event.getId(),
                        ticketType.getId(),
                        seller.getId()
                )
        );

        assertEquals("No more tickets available for this ticket type", exception.getMessage());
    }

    @Test
    void shouldCalculateRemainingStockCorrectly() {
        Event event = createEvent();
        TicketType ticketType = createTicketType(event, 10);
        User seller = createUser("stock-remaining@email.com");

        ticketSaleService.createTicketSale(
                createSale("Confirmed Buyer", 3),
                event.getId(),
                ticketType.getId(),
                seller.getId()
        );
        ticketSaleService.createTicketSale(
                createCancelledSale("Cancelled Buyer", 2),
                event.getId(),
                ticketType.getId(),
                seller.getId()
        );

        TicketTypeResponse response = ticketTypeService.getTicketTypeResponsesByEventId(event.getId()).stream()
                .filter(ticketTypeResponse -> ticketTypeResponse.id().equals(ticketType.getId()))
                .findFirst()
                .orElseThrow();

        assertEquals(10, response.availableQuantity());
        assertEquals(7, response.remainingQuantity());
    }

    private Event createEvent() {
        Event event = new Event();
        event.setTitle("Stock Event");
        event.setStatus(OPEN);

        return eventRepository.save(event);
    }

    private TicketType createTicketType(Event event, int availableQuantity) {
        TicketType ticketType = new TicketType();
        ticketType.setEvent(event);
        ticketType.setName("General Admission");
        ticketType.setPrice(new BigDecimal("25.00"));
        ticketType.setAvailableQuantity(availableQuantity);

        return ticketTypeRepository.save(ticketType);
    }

    private User createUser(String email) {
        User user = new User();
        user.setName("Seller");
        user.setEmail(email);
        user.setRole(VOLUNTEER);

        return userRepository.save(user);
    }

    private TicketSale createSale(String buyerName, int quantity) {
        TicketSale sale = new TicketSale();
        sale.setBuyerName(buyerName);
        sale.setQuantity(quantity);

        return sale;
    }

    private TicketSale createCancelledSale(String buyerName, int quantity) {
        TicketSale sale = createSale(buyerName, quantity);
        sale.setStatus(CANCELLED);

        return sale;
    }
}
