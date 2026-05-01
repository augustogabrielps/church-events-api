package com.serve.service;

import com.serve.domain.Event;
import com.serve.domain.Payment;
import com.serve.domain.TicketSale;
import com.serve.domain.TicketType;
import com.serve.domain.User;
import com.serve.exception.BusinessRuleException;
import com.serve.repository.EventRepository;
import com.serve.repository.TicketSaleRepository;
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
import static com.serve.domain.UserRole.VOLUNTEER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TicketFinancialFlowServiceTest {

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

    @Autowired
    private TicketSaleRepository ticketSaleRepository;

    @Test
    void shouldCalculateTotalAmountCorrectly() {
        Event event = createEvent();
        TicketType ticketType = createTicketType(event, "25.50");
        User seller = createUser("seller-total@email.com");

        TicketSale sale = ticketSaleService.createTicketSale(
                createSale("Buyer", 3),
                event.getId(),
                ticketType.getId(),
                seller.getId()
        );

        assertEquals(new BigDecimal("76.50"), sale.getTotalAmount());
    }

    @Test
    void shouldAllowPartialPayment() {
        TicketSale sale = createPersistedSale("seller-partial@email.com", "50.00", 2);

        paymentService.createPayment(sale.getId(), createPayment("40.00", CONFIRMED));

        TicketSale updatedSale = ticketSaleRepository.findById(sale.getId()).orElseThrow();

        assertEquals(new BigDecimal("40.00"), paymentService.getTotalPaid(sale.getId()));
        assertEquals(com.serve.domain.TicketSaleStatus.PENDING, updatedSale.getStatus());
    }

    @Test
    void shouldConfirmSaleWhenFullyPaid() {
        TicketSale sale = createPersistedSale("seller-full@email.com", "50.00", 2);

        paymentService.createPayment(sale.getId(), createPayment("100.00", CONFIRMED));

        TicketSale updatedSale = ticketSaleRepository.findById(sale.getId()).orElseThrow();

        assertEquals(new BigDecimal("100.00"), paymentService.getTotalPaid(sale.getId()));
        assertEquals(com.serve.domain.TicketSaleStatus.CONFIRMED, updatedSale.getStatus());
    }

    @Test
    void shouldRejectOverpayment() {
        TicketSale sale = createPersistedSale("seller-overpayment@email.com", "50.00", 2);

        paymentService.createPayment(sale.getId(), createPayment("70.00", CONFIRMED));

        assertThrows(
                BusinessRuleException.class,
                () -> paymentService.createPayment(sale.getId(), createPayment("31.00", CONFIRMED))
        );
    }

    @Test
    void shouldIgnorePendingPaymentsInTotal() {
        TicketSale sale = createPersistedSale("seller-pending@email.com", "50.00", 2);

        paymentService.createPayment(sale.getId(), createPayment("100.00", PENDING));

        TicketSale updatedSale = ticketSaleRepository.findById(sale.getId()).orElseThrow();

        assertEquals(BigDecimal.ZERO, paymentService.getTotalPaid(sale.getId()));
        assertEquals(com.serve.domain.TicketSaleStatus.PENDING, updatedSale.getStatus());
    }

    private TicketSale createPersistedSale(String sellerEmail, String ticketPrice, int quantity) {
        Event event = createEvent();
        TicketType ticketType = createTicketType(event, ticketPrice);
        User seller = createUser(sellerEmail);

        return ticketSaleService.createTicketSale(
                createSale("Buyer", quantity),
                event.getId(),
                ticketType.getId(),
                seller.getId()
        );
    }

    private Event createEvent() {
        Event event = new Event();
        event.setTitle("Ticket Event");
        event.setStatus(OPEN);

        return eventRepository.save(event);
    }

    private TicketType createTicketType(Event event, String price) {
        TicketType ticketType = new TicketType();
        ticketType.setEvent(event);
        ticketType.setName("General Admission");
        ticketType.setPrice(new BigDecimal(price));
        ticketType.setAvailableQuantity(100);

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

    private Payment createPayment(String amount, com.serve.domain.PaymentStatus status) {
        Payment payment = new Payment();
        payment.setAmount(new BigDecimal(amount));
        payment.setMethod(PIX);
        payment.setStatus(status);

        return payment;
    }
}
