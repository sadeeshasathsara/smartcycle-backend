package com.smartcycle.smartcycleapplication.repository;

import com.smartcycle.smartcycleapplication.entity.PickupRequest;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PickupRequestRepository extends JpaRepository<PickupRequest, Long> {
    long countByPreferredDateTimeBetween(LocalDateTime start, LocalDateTime end);
}


