package com.smartcycle.smartcycleapplication.repositories;

import com.smartcycle.smartcycleapplication.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Custom query to find any user by their email address
    Optional<User> findByEmail(String email);
}
