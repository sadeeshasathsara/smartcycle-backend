package com.smartcycle.smartcycleapplication.services;

import com.smartcycle.smartcycleapplication.dtos.CollectionHistoryDTO;
import com.smartcycle.smartcycleapplication.dtos.OutstandingBalanceDTO;
import com.smartcycle.smartcycleapplication.dtos.PaymentReceiptDTO;
import com.smartcycle.smartcycleapplication.models.*;
import com.smartcycle.smartcycleapplication.repositories.PaymentRepository;
import com.smartcycle.smartcycleapplication.repositories.ResidentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.smartcycle.smartcycleapplication.models.Status;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final ResidentRepository residentRepository;
    private final PaymentRepository paymentRepository;

    public PaymentService(ResidentRepository residentRepository, PaymentRepository paymentRepository) {
        this.residentRepository = residentRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public OutstandingBalanceDTO getOutstandingBalance(String userEmail) {
        Resident resident = residentRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("Resident not found for the logged-in user."));

        // Find PENDING payments for COMPLETED requests
        List<Payment> pendingPayments = paymentRepository.findPendingPaymentsForCompletedRequests(
                resident.getId(),
                PaymentStatus.PENDING
        );

        // Calculate the initial subtotal
        double subtotal = pendingPayments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();

        // Calculate and apply rebates
        double totalRebates = pendingPayments.stream()
                .filter(payment -> payment.getRecyclingRebate() != null)
                .mapToDouble(payment -> payment.getRecyclingRebate().getAmount())
                .sum();

        double finalBalance = subtotal - totalRebates;

        // --- NEW: Save the calculated balance to the resident's account ---
        resident.setAccountBalance(finalBalance);
        residentRepository.save(resident);
        // --- End of New Logic ---

        // Map the associated collection requests to DTOs for the response
        List<CollectionHistoryDTO> unpaidRequestDTOs = pendingPayments.stream()
                .map(payment -> mapToCollectionHistoryDTO(payment.getCollectionRequest()))
                .collect(Collectors.toList());

        OutstandingBalanceDTO balanceDTO = new OutstandingBalanceDTO();
        balanceDTO.setTotalOutstanding(finalBalance);
        balanceDTO.setUnpaidRequests(unpaidRequestDTOs);

        return balanceDTO;
    }

    @Transactional
    public PaymentReceiptDTO processPayment(String userEmail) {
        Resident resident = residentRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("Resident not found."));

        // 1. Find all payments that need to be processed
        List<Payment> paymentsToProcess = paymentRepository.findPendingPaymentsForCompletedRequests(
                resident.getId(),
                PaymentStatus.PENDING
        );

        if (paymentsToProcess.isEmpty()) {
            throw new IllegalStateException("No outstanding balance to pay.");
        }

        // 2. Calculate the total amount paid (including rebates)
        double amountPaid = resident.getAccountBalance();

        // 3. Simulate payment by updating the status of each payment record
        for (Payment payment : paymentsToProcess) {
            payment.setStatus(PaymentStatus.PAID);
        }
        paymentRepository.saveAll(paymentsToProcess);

        // 4. Update the resident's account balance to zero
        resident.setAccountBalance(0.0);
        residentRepository.save(resident);

        // 5. Generate and return a digital receipt
        return createReceipt(resident, amountPaid, paymentsToProcess);
    }

    private PaymentReceiptDTO createReceipt(Resident resident, double amountPaid, List<Payment> paidItems) {
        PaymentReceiptDTO receipt = new PaymentReceiptDTO();
        receipt.setTransactionId("TXN-" + UUID.randomUUID().toString());
        receipt.setAmountPaid(amountPaid);
        receipt.setPaymentDate(LocalDateTime.now());
        receipt.setResidentName(resident.getName());
        receipt.setPaidRequestIds(
                paidItems.stream().map(p -> p.getCollectionRequest().getId()).toList()
        );
        return receipt;
    }

    // Helper method to convert a CollectionRequest to a DTO
    private CollectionHistoryDTO mapToCollectionHistoryDTO(CollectionRequest request) {
        CollectionHistoryDTO dto = new CollectionHistoryDTO();
        dto.setRequestId(request.getId());
        dto.setWasteType(request.getWasteType());
        dto.setQuantity(request.getQuantity());
        dto.setStatus(request.getStatus());
        dto.setScheduledDate(request.getScheduleDate());
        return dto;
    }
}