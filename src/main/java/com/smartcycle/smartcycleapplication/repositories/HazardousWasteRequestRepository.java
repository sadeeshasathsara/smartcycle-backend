package com.smartcycle.smartcycleapplication.repositories;

import com.smartcycle.smartcycleapplication.models.HazardousWasteRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HazardousWasteRequestRepository extends JpaRepository<HazardousWasteRequest, Long> {
    // Queries specific to hazardous waste requests can be added here.
}