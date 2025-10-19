package com.smartcycle.smartcycleapplication;

import com.smartcycle.smartcycleapplication.dtos.CollectionHistoryDTO;
import com.smartcycle.smartcycleapplication.dtos.OutstandingBalanceDTO;
import com.smartcycle.smartcycleapplication.dtos.PaymentReceiptDTO;
import com.smartcycle.smartcycleapplication.models.*;
import com.smartcycle.smartcycleapplication.repositories.PaymentRepository;
import com.smartcycle.smartcycleapplication.repositories.ResidentRepository;
import com.smartcycle.smartcycleapplication.services.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Use Mockito with JUnit 5
class PaymentServiceTest {

    @Mock // Create a mock instance of PaymentRepository
    private PaymentRepository paymentRepository;

    @Mock // Create a mock instance of ResidentRepository
    private ResidentRepository residentRepository;

    @InjectMocks // Create an instance of PaymentService and inject the mocks into it
    private PaymentService paymentService;

    private Resident testResident;
    private CollectionRequest testRequest1;
    private CollectionRequest testRequest2;
    private Payment testPayment1;
    private Payment testPayment2;
    private RecyclingRebate testRebate;

    @BeforeEach
    void setUp() {
        // Set up common test data before each test method
        testResident = new Resident();
        testResident.setId(1L);
        testResident.setEmail("test@example.com");
        testResident.setName("Test Resident");
        testResident.setAccountBalance(0.0); // Start with zero balance

        testRequest1 = new CollectionRequest();
        testRequest1.setId(10L);
        testRequest1.setResident(testResident);
        testRequest1.setStatus(Status.COMPLETED); // Important: Must be COMPLETED for balance calculation
        testRequest1.setWasteType(WasteType.HOUSEHOLD_WASTE);

        testRequest2 = new CollectionRequest();
        testRequest2.setId(11L);
        testRequest2.setResident(testResident);
        testRequest2.setStatus(Status.COMPLETED); // Important: Must be COMPLETED
        testRequest2.setWasteType(WasteType.RECYCLABLE);

        testPayment1 = new Payment();
        testPayment1.setId(100L);
        testPayment1.setAmount(50.0);
        testPayment1.setStatus(PaymentStatus.PENDING);
        testPayment1.setCollectionRequest(testRequest1);

        testPayment2 = new Payment();
        testPayment2.setId(101L);
        testPayment2.setAmount(75.0);
        testPayment2.setStatus(PaymentStatus.PENDING);
        testPayment2.setCollectionRequest(testRequest2);

        testRebate = new RecyclingRebate();
        testRebate.setId(200L);
        testRebate.setAmount(10.0);
        testRebate.setPayment(testPayment2);
    }

    // --- Tests for getOutstandingBalance ---

    @Test
    void getOutstandingBalance_ShouldCalculateCorrectTotal_WhenPaymentsExistWithoutRebate() {
        // Arrange
        String userEmail = "test@example.com";
        when(residentRepository.findByEmail(userEmail)).thenReturn(Optional.of(testResident));
        // Simulate finding only the payment without a rebate
        when(paymentRepository.findPendingPaymentsForCompletedRequests(testResident.getId(), PaymentStatus.PENDING))
                .thenReturn(List.of(testPayment1));

        // Act
        OutstandingBalanceDTO result = paymentService.getOutstandingBalance(userEmail);

        // Assert
        assertNotNull(result);
        assertEquals(50.0, result.getTotalOutstanding()); // Only payment1 amount
        assertEquals(1, result.getUnpaidRequests().size());
        assertEquals(testRequest1.getId(), result.getUnpaidRequests().get(0).getRequestId());

        // Verify that the resident's balance was updated and saved
        ArgumentCaptor<Resident> residentCaptor = ArgumentCaptor.forClass(Resident.class);
        verify(residentRepository, times(1)).save(residentCaptor.capture());
        assertEquals(50.0, residentCaptor.getValue().getAccountBalance());
    }

    @Test
    void getOutstandingBalance_ShouldCalculateCorrectTotal_WhenPaymentsExistWithRebate() {
        // Arrange
        String userEmail = "test@example.com";
        testPayment2.setRecyclingRebate(testRebate); // Add the rebate to payment2
        when(residentRepository.findByEmail(userEmail)).thenReturn(Optional.of(testResident));
        // Simulate finding both payments, one with a rebate
        when(paymentRepository.findPendingPaymentsForCompletedRequests(testResident.getId(), PaymentStatus.PENDING))
                .thenReturn(List.of(testPayment1, testPayment2));

        // Act
        OutstandingBalanceDTO result = paymentService.getOutstandingBalance(userEmail);

        // Assert
        assertNotNull(result);
        assertEquals(115.0, result.getTotalOutstanding()); // 50.0 + 75.0 - 10.0 (rebate)
        assertEquals(2, result.getUnpaidRequests().size());

        // Verify resident balance was updated correctly
        ArgumentCaptor<Resident> residentCaptor = ArgumentCaptor.forClass(Resident.class);
        verify(residentRepository, times(1)).save(residentCaptor.capture());
        assertEquals(115.0, residentCaptor.getValue().getAccountBalance());
    }

    @Test
    void getOutstandingBalance_ShouldReturnZero_WhenNoPendingPaymentsFound() {
        // Arrange
        String userEmail = "test@example.com";
        when(residentRepository.findByEmail(userEmail)).thenReturn(Optional.of(testResident));
        // Simulate finding no pending payments
        when(paymentRepository.findPendingPaymentsForCompletedRequests(testResident.getId(), PaymentStatus.PENDING))
                .thenReturn(Collections.emptyList());

        // Act
        OutstandingBalanceDTO result = paymentService.getOutstandingBalance(userEmail);

        // Assert
        assertNotNull(result);
        assertEquals(0.0, result.getTotalOutstanding());
        assertTrue(result.getUnpaidRequests().isEmpty());

        // Verify balance was updated to 0
        ArgumentCaptor<Resident> residentCaptor = ArgumentCaptor.forClass(Resident.class);
        verify(residentRepository, times(1)).save(residentCaptor.capture());
        assertEquals(0.0, residentCaptor.getValue().getAccountBalance());
    }

    @Test
    void getOutstandingBalance_ShouldThrowException_WhenResidentNotFound() {
        // Arrange
        String userEmail = "nonexistent@example.com";
        when(residentRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            paymentService.getOutstandingBalance(userEmail);
        });
        assertEquals("Resident not found for the logged-in user.", exception.getMessage());
        verify(residentRepository, never()).save(any(Resident.class)); // Ensure save wasn't called
    }


    // --- Tests for processPayment ---

    @Test
    void processPayment_ShouldUpdateStatusAndBalance_WhenPendingPaymentsExist() {
        // Arrange
        String userEmail = "test@example.com";
        testResident.setAccountBalance(125.0); // Set a balance from a previous getOutstandingBalance call
        when(residentRepository.findByEmail(userEmail)).thenReturn(Optional.of(testResident));
        List<Payment> pending = List.of(testPayment1, testPayment2);
        when(paymentRepository.findPendingPaymentsForCompletedRequests(testResident.getId(), PaymentStatus.PENDING))
                .thenReturn(pending);

        // Act
        PaymentReceiptDTO receipt = paymentService.processPayment(userEmail);

        // Assert
        assertNotNull(receipt);
        assertEquals(testResident.getName(), receipt.getResidentName());
        assertEquals(125.0, receipt.getAmountPaid()); // Amount paid is the balance *before* clearing
        assertEquals(2, receipt.getPaidRequestIds().size());
        assertTrue(receipt.getPaidRequestIds().containsAll(List.of(10L, 11L)));

        // Verify payments were updated to PAID
        ArgumentCaptor<List<Payment>> paymentsCaptor = ArgumentCaptor.forClass(List.class);
        verify(paymentRepository, times(1)).saveAll(paymentsCaptor.capture());
        List<Payment> savedPayments = paymentsCaptor.getValue();
        assertEquals(2, savedPayments.size());
        assertTrue(savedPayments.stream().allMatch(p -> p.getStatus() == PaymentStatus.PAID));

        // Verify resident balance was updated to 0.0
        ArgumentCaptor<Resident> residentCaptor = ArgumentCaptor.forClass(Resident.class);
        verify(residentRepository, times(1)).save(residentCaptor.capture());
        assertEquals(0.0, residentCaptor.getValue().getAccountBalance());
    }

    @Test
    void processPayment_ShouldThrowException_WhenNoPendingPaymentsExist() {
        // Arrange
        String userEmail = "test@example.com";
        when(residentRepository.findByEmail(userEmail)).thenReturn(Optional.of(testResident));
        when(paymentRepository.findPendingPaymentsForCompletedRequests(testResident.getId(), PaymentStatus.PENDING))
                .thenReturn(Collections.emptyList()); // No pending payments

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            paymentService.processPayment(userEmail);
        });
        assertEquals("No outstanding balance to pay.", exception.getMessage());
        verify(paymentRepository, never()).saveAll(anyList()); // Ensure no payments were saved
        verify(residentRepository, never()).save(any(Resident.class)); // Ensure resident wasn't saved
    }

    @Test
    void processPayment_ShouldThrowException_WhenResidentNotFound() {
        // Arrange
        String userEmail = "nonexistent@example.com";
        when(residentRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            paymentService.processPayment(userEmail);
        });
        assertEquals("Resident not found.", exception.getMessage()); // Adjusted expected message
        verify(paymentRepository, never()).saveAll(anyList());
        verify(residentRepository, never()).save(any(Resident.class));
    }
}
