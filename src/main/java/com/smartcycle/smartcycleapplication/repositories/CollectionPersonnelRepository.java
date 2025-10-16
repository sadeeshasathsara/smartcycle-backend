package com.smartcycle.smartcycleapplication.repositories;

import com.smartcycle.smartcycleapplication.models.CollectionPersonnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollectionPersonnelRepository extends JpaRepository<CollectionPersonnel, Long> {
    Optional<CollectionPersonnel> findByEmail(String email);
}
