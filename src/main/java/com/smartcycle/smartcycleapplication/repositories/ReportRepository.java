package com.smartcycle.smartcycleapplication.repositories;

import com.smartcycle.smartcycleapplication.models.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    // Report-specific queries can be added here.
}