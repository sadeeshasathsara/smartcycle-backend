package com.smartcycle.smartcycleapplication.repositories;

import com.smartcycle.smartcycleapplication.models.Resident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Import Optional

@Repository
public interface ResidentRepository extends JpaRepository<Resident, Long> {

    // Add this method signature
    Optional<Resident> findByEmail(String email);

}