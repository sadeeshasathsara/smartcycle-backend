package com.smartcycle.smartcycleapplication.controllers;

import com.smartcycle.smartcycleapplication.dtos.OutstandingBalanceDTO;
import com.smartcycle.smartcycleapplication.dtos.PaymentReceiptDTO;
import com.smartcycle.smartcycleapplication.services.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/balance")
    public ResponseEntity<OutstandingBalanceDTO> getMyBalance(@AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        OutstandingBalanceDTO balance = paymentService.getOutstandingBalance(userEmail);
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/pay")
    public ResponseEntity<?> makePayment(@AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        PaymentReceiptDTO receipt = paymentService.processPayment(userEmail);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Payment successful.",
                        "data", receipt
                )
        );
    }
}