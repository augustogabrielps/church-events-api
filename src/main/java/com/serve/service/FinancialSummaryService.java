package com.serve.service;

import com.serve.domain.PaymentStatus;
import com.serve.dto.FinancialSummaryResponse;
import com.serve.repository.EventRepository;
import com.serve.repository.PaymentRepository;
import com.serve.repository.TicketSaleRepository;
import com.serve.repository.TicketTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class FinancialSummaryService {

    private final EventRepository eventRepository;
    private final TicketSaleRepository ticketSaleRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final PaymentRepository paymentRepository;

    public FinancialSummaryService(
            EventRepository eventRepository,
            TicketSaleRepository ticketSaleRepository,
            TicketTypeRepository ticketTypeRepository,
            PaymentRepository paymentRepository
    ) {
        this.eventRepository = eventRepository;
        this.ticketSaleRepository = ticketSaleRepository;
        this.ticketTypeRepository = ticketTypeRepository;
        this.paymentRepository = paymentRepository;
    }

    public FinancialSummaryResponse getFinancialSummary(UUID eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Event not found");
        }

        BigDecimal totalSalesAmount = ticketSaleRepository.sumTotalSalesByEventId(eventId);
        BigDecimal totalPaidAmount = paymentRepository.sumPaymentsByEventIdAndStatus(
                eventId,
                PaymentStatus.CONFIRMED
        );
        int totalTicketsSold = ticketSaleRepository.sumQuantityByEventId(eventId);
        int totalAvailableTickets = ticketTypeRepository.sumAvailableQuantityByEventId(eventId);

        return new FinancialSummaryResponse(
                eventId,
                totalSalesAmount,
                totalPaidAmount,
                totalSalesAmount.subtract(totalPaidAmount),
                totalTicketsSold,
                totalAvailableTickets - totalTicketsSold
        );
    }
}
