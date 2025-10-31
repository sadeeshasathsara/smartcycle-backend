package com.smartcycle.smartcycleapplication.repositories;

import com.smartcycle.smartcycleapplication.models.WasteBin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WasteBinRepository extends JpaRepository<WasteBin, Long> {
    // Find all bins belonging to a specific resident
    List<WasteBin> findByResidentId(Long residentId);
}