package com.smartcycle.smartcycleapplication.repositories;

import com.smartcycle.smartcycleapplication.models.RecyclingRebate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecyclingRebateRepository extends JpaRepository<RecyclingRebate, Long> {
    // Rebate-specific queries can be added here.
}