package com.serve.repository;

import com.serve.domain.Payment;
import com.serve.domain.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findBySale_Id(UUID saleId);

    @Query("""
            select coalesce(sum(payment.amount), 0)
            from Payment payment
            where payment.sale.id = :saleId
              and payment.status = :status
            """)
    BigDecimal sumAmountBySaleIdAndStatus(
            @Param("saleId") UUID saleId,
            @Param("status") PaymentStatus status
    );

    @Query("""
            select coalesce(sum(payment.amount), 0)
            from Payment payment
            where payment.sale.event.id = :eventId
              and payment.sale.status <> com.serve.domain.TicketSaleStatus.CANCELLED
              and payment.status = :status
            """)
    BigDecimal sumPaymentsByEventIdAndStatus(
            @Param("eventId") UUID eventId,
            @Param("status") PaymentStatus status
    );
}
