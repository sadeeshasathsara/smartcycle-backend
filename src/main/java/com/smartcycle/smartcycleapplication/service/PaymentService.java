package com.smartcycle.smartcycleapplication.service;

import com.smartcycle.smartcycleapplication.entity.Payment;
import com.smartcycle.smartcycleapplication.entity.PickupRequest;
import com.smartcycle.smartcycleapplication.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment createPayment(PickupRequest pickupRequest, String paymentMethod) {
        Payment payment = new Payment();
        payment.setPickupRequest(pickupRequest);
        payment.setAmount(pickupRequest.getFee());
        payment.setStatus("PENDING");
        payment.setPaymentMethod(paymentMethod);
        payment.setCreatedAt(LocalDateTime.now());
        
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment completePayment(Long paymentId, String transactionId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        payment.setStatus("COMPLETED");
        payment.setTransactionId(transactionId);
        payment.setCompletedAt(LocalDateTime.now());
        
        return paymentRepository.save(payment);
    }
}
