package com.smartcycle.smartcycleapplication.repositories;

import com.smartcycle.smartcycleapplication.models.Payment;
import com.smartcycle.smartcycleapplication.models.PaymentStatus;
import com.smartcycle.smartcycleapplication.models.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByCollectionRequestId(Long collectionRequestId);

    @Query("SELECT p FROM Payment p WHERE p.collectionRequest.resident.id = :residentId AND p.status = :paymentStatus")
    List<Payment> findPendingPaymentsForCompletedRequests(
            @Param("residentId") Long residentId,
            @Param("paymentStatus") PaymentStatus paymentStatus
    );
}