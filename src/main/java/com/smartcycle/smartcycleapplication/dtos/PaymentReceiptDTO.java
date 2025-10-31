package com.smartcycle.smartcycleapplication.dtos;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PaymentReceiptDTO {
    private String transactionId;
    private double amountPaid;
    private LocalDateTime paymentDate;
    private String residentName;
    private List<Long> paidRequestIds;
}