package com.smartcycle.smartcycleapplication.dtos;

import lombok.Data;

@Data
public class PaymentRequestDTO {
    // For now, we'll assume a simple payment method identifier.
    // In a real system, this would be more complex (e.g., card token).
    private String paymentMethod;
    // We don't need an amount, as we will pay the full outstanding balance.
}