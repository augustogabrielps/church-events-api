package com.serve.repository;

import com.serve.domain.TicketSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface TicketSaleRepository extends JpaRepository<TicketSale, UUID> {

    List<TicketSale> findByEvent_Id(UUID eventId);

    @Query("""
            select coalesce(sum(sale.quantity), 0)
            from TicketSale sale
            where sale.ticketType.id = :ticketTypeId
              and sale.status <> com.serve.domain.TicketSaleStatus.CANCELLED
            """)
    int sumQuantityByTicketTypeId(@Param("ticketTypeId") UUID ticketTypeId);

    @Query("""
            select coalesce(sum(sale.totalAmount), 0)
            from TicketSale sale
            where sale.event.id = :eventId
              and sale.status <> com.serve.domain.TicketSaleStatus.CANCELLED
            """)
    BigDecimal sumTotalSalesByEventId(@Param("eventId") UUID eventId);

    @Query("""
            select coalesce(sum(sale.quantity), 0)
            from TicketSale sale
            where sale.event.id = :eventId
              and sale.status <> com.serve.domain.TicketSaleStatus.CANCELLED
            """)
    int sumQuantityByEventId(@Param("eventId") UUID eventId);
}
