package com.smartcycle.smartcycleapplication.repositories;

import com.smartcycle.smartcycleapplication.models.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    // Admin-specific queries can be added here.
}
