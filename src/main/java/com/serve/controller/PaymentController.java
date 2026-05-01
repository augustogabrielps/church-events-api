package com.serve.controller;

import com.serve.domain.Payment;
import com.serve.dto.CreatePaymentRequest;
import com.serve.dto.PaymentResponse;
import com.serve.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ticket-sales/{saleId}/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @PathVariable UUID saleId,
            @Valid @RequestBody CreatePaymentRequest request
    ) {
        Payment payment = new Payment();
        payment.setAmount(request.amount());
        payment.setMethod(request.method());
        payment.setStatus(request.status());
        payment.setPaidAt(request.paidAt());

        Payment createdPayment = paymentService.createPayment(saleId, payment);

        return ResponseEntity
                .created(URI.create("/ticket-sales/" + saleId + "/payments/" + createdPayment.getId()))
                .body(PaymentResponse.from(createdPayment));
    }

    @GetMapping
    public List<PaymentResponse> getPaymentsBySale(@PathVariable UUID saleId) {
        return paymentService.getPaymentsBySaleId(saleId).stream()
                .map(PaymentResponse::from)
                .toList();
    }
}
