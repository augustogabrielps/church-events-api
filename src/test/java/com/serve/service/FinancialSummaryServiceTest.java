package com.serve.service;

import com.serve.domain.Event;
import com.serve.domain.Payment;
import com.serve.domain.TicketSale;
import com.serve.domain.TicketType;
import com.serve.domain.User;
import com.serve.dto.FinancialSummaryResponse;
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
import static com.serve.domain.PaymentMethod.PIX;
import static com.serve.domain.PaymentStatus.CONFIRMED;
import static com.serve.domain.PaymentStatus.PENDING;
import static com.serve.domain.TicketSaleStatus.CANCELLED;
import static com.serve.domain.UserRole.VOLUNTEER;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FinancialSummaryServiceTest {

    @Autowired
    private FinancialSummaryService financialSummaryService;

    @Autowired
    private TicketSaleService ticketSaleService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TicketTypeRepository ticketTypeRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldCalculateFinancialSummaryCorrectly() {
        Event event = createEvent();
        TicketType generalAdmission = createTicketType(event, "30.00", 10);
        TicketType vip = createTicketType(event, "20.00", 5);
        User seller = createUser("summary-seller@email.com");

        TicketSale firstSale = ticketSaleService.createTicketSale(
                createSale("First Buyer", 2),
                event.getId(),
                generalAdmission.getId(),
                seller.getId()
        );
        TicketSale secondSale = ticketSaleService.createTicketSale(
                createSale("Second Buyer", 3),
                event.getId(),
                vip.getId(),
                seller.getId()
        );
        ticketSaleService.createTicketSale(
                createCancelledSale("Cancelled Buyer", 1),
                event.getId(),
                generalAdmission.getId(),
                seller.getId()
        );

        paymentService.createPayment(firstSale.getId(), createPayment("50.00", CONFIRMED));
        paymentService.createPayment(secondSale.getId(), createPayment("40.00", PENDING));

        FinancialSummaryResponse response = financialSummaryService.getFinancialSummary(event.getId());

        assertEquals(event.getId(), response.eventId());
        assertEquals(new BigDecimal("120.00"), response.totalSalesAmount());
        assertEquals(new BigDecimal("50.00"), response.totalPaidAmount());
        assertEquals(new BigDecimal("70.00"), response.totalPendingAmount());
        assertEquals(5, response.totalTicketsSold());
        assertEquals(10, response.remainingTickets());
    }

    private Event createEvent() {
        Event event = new Event();
        event.setTitle("Financial Summary Event");
        event.setStatus(OPEN);

        return eventRepository.save(event);
    }

    private TicketType createTicketType(Event event, String price, int availableQuantity) {
        TicketType ticketType = new TicketType();
        ticketType.setEvent(event);
        ticketType.setName("Ticket " + price);
        ticketType.setPrice(new BigDecimal(price));
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

    private Payment createPayment(String amount, com.serve.domain.PaymentStatus status) {
        Payment payment = new Payment();
        payment.setAmount(new BigDecimal(amount));
        payment.setMethod(PIX);
        payment.setStatus(status);

        return payment;
    }
}
