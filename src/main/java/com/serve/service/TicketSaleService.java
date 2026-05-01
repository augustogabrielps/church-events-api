package com.serve.service;

import com.serve.domain.Event;
import com.serve.domain.PaymentStatus;
import com.serve.domain.TicketSale;
import com.serve.domain.TicketSaleStatus;
import com.serve.domain.TicketType;
import com.serve.domain.User;
import com.serve.exception.BusinessRuleException;
import com.serve.repository.EventRepository;
import com.serve.repository.PaymentRepository;
import com.serve.repository.TicketSaleRepository;
import com.serve.repository.TicketTypeRepository;
import com.serve.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TicketSaleService {

    private final EventRepository eventRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final UserRepository userRepository;
    private final TicketSaleRepository ticketSaleRepository;
    private final PaymentRepository paymentRepository;

    public TicketSaleService(
            EventRepository eventRepository,
            TicketTypeRepository ticketTypeRepository,
            UserRepository userRepository,
            TicketSaleRepository ticketSaleRepository,
            PaymentRepository paymentRepository
    ) {
        this.eventRepository = eventRepository;
        this.ticketTypeRepository = ticketTypeRepository;
        this.userRepository = userRepository;
        this.ticketSaleRepository = ticketSaleRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public TicketSale createTicketSale(TicketSale sale, UUID eventId, UUID ticketTypeId, UUID sellerId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        TicketType ticketType = ticketTypeRepository.findById(ticketTypeId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket type not found"));

        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new EntityNotFoundException("Seller not found"));

        if (!ticketType.getEvent().getId().equals(event.getId())) {
            throw new BusinessRuleException("Ticket type must belong to the selected event");
        }

        if (sale.getQuantity() == null || sale.getQuantity() <= 0) {
            throw new BusinessRuleException("Ticket sale quantity must be greater than zero");
        }

        int totalSold = ticketSaleRepository.sumQuantityByTicketTypeId(ticketTypeId);
        if (totalSold + sale.getQuantity() > ticketType.getAvailableQuantity()) {
            throw new BusinessRuleException("No more tickets available for this ticket type");
        }

        if (ticketType.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleException("Ticket type price cannot be negative");
        }

        if (sale.getStatus() == null) {
            sale.setStatus(TicketSaleStatus.PENDING);
        }

        sale.setEvent(event);
        sale.setTicketType(ticketType);
        sale.setSeller(seller);
        sale.setTotalAmount(ticketType.getPrice().multiply(BigDecimal.valueOf(sale.getQuantity())));

        return ticketSaleRepository.save(sale);
    }

    public List<TicketSale> getTicketSalesByEventId(UUID eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Event not found");
        }

        return ticketSaleRepository.findByEvent_Id(eventId);
    }

    public BigDecimal getRemainingAmount(UUID saleId) {
        TicketSale sale = ticketSaleRepository.findById(saleId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket sale not found"));

        return sale.getTotalAmount().subtract(
                paymentRepository.sumAmountBySaleIdAndStatus(saleId, PaymentStatus.CONFIRMED)
        );
    }
}
