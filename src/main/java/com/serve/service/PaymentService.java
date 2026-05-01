package com.serve.service;

import com.serve.domain.Payment;
import com.serve.domain.PaymentStatus;
import com.serve.domain.TicketSale;
import com.serve.domain.TicketSaleStatus;
import com.serve.exception.BusinessRuleException;
import com.serve.repository.PaymentRepository;
import com.serve.repository.TicketSaleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final TicketSaleRepository ticketSaleRepository;
    private final PaymentRepository paymentRepository;

    public PaymentService(TicketSaleRepository ticketSaleRepository, PaymentRepository paymentRepository) {
        this.ticketSaleRepository = ticketSaleRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment createPayment(UUID saleId, Payment payment) {
        TicketSale sale = ticketSaleRepository.findById(saleId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket sale not found"));

        if (payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Payment amount must be greater than zero");
        }

        if (payment.getStatus() == null) {
            payment.setStatus(PaymentStatus.PENDING);
        }

        if (payment.getStatus() == PaymentStatus.CONFIRMED) {
            validatePaymentDoesNotExceedSaleTotal(sale, payment.getAmount());
        }

        payment.setSale(sale);

        Payment savedPayment = paymentRepository.save(payment);
        updateSaleStatus(sale);

        return savedPayment;
    }

    public List<Payment> getPaymentsBySaleId(UUID saleId) {
        if (!ticketSaleRepository.existsById(saleId)) {
            throw new EntityNotFoundException("Ticket sale not found");
        }

        return paymentRepository.findBySale_Id(saleId);
    }

    public BigDecimal getTotalPaid(UUID saleId) {
        return paymentRepository.sumAmountBySaleIdAndStatus(saleId, PaymentStatus.CONFIRMED);
    }

    private void validatePaymentDoesNotExceedSaleTotal(TicketSale sale, BigDecimal paymentAmount) {
        BigDecimal totalAfterPayment = getTotalPaid(sale.getId()).add(paymentAmount);

        if (totalAfterPayment.compareTo(sale.getTotalAmount()) > 0) {
            throw new BusinessRuleException("Payment exceeds total sale amount");
        }
    }

    private void updateSaleStatus(TicketSale sale) {
        BigDecimal totalPaid = getTotalPaid(sale.getId());

        if (totalPaid.compareTo(sale.getTotalAmount()) == 0) {
            sale.setStatus(TicketSaleStatus.CONFIRMED);
        } else {
            sale.setStatus(TicketSaleStatus.PENDING);
        }

        ticketSaleRepository.save(sale);
    }
}
