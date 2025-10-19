package com.smartcycle.smartcycleapplication.repositories;

import com.smartcycle.smartcycleapplication.models.CollectionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.smartcycle.smartcycleapplication.models.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional; // Make sure to import Optional

@Repository
public interface CollectionRequestRepository extends JpaRepository<CollectionRequest, Long> {

    List<CollectionRequest> findByResidentIdOrderByScheduleDateDesc(Long residentId);

    // Add this new method signature
    Optional<CollectionRequest> findByIdAndResidentId(Long id, Long residentId);

    // --- You can remove the older, less specific methods if you want ---
    // List<CollectionRequest> findByResidentId(Long residentId);
    // List<CollectionRequest> findByStatus(String status);
    List<CollectionRequest> findByResidentIdAndStatusAndPaymentStatus(Long residentId, Status status, String paymentStatus);
    Optional<CollectionRequest> findFirstByResidentIdAndStatusInOrderByScheduleDateAsc(Long residentId, List<Status> statuses);
    List<CollectionRequest> findByCollectionScheduleId(Long scheduleId);
    List<CollectionRequest> findByResidentIdAndStatusInOrderByScheduleDateAsc(Long residentId, List<Status> statuses);
    List<CollectionRequest> findByStatusAndScheduleDateBetweenAndCollectionScheduleIsNull(
            Status status,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

}