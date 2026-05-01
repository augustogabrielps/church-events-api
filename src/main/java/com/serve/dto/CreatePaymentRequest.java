package com.serve.dto;

import com.serve.domain.PaymentMethod;
import com.serve.domain.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreatePaymentRequest(
        @NotNull @DecimalMin(value = "0.00", inclusive = false) BigDecimal amount,
        @NotNull PaymentMethod method,
        PaymentStatus status,
        LocalDateTime paidAt
) {
}
