package com.smartcycle.smartcycleapplication.repositories;

import com.smartcycle.smartcycleapplication.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Find all notifications for a specific user
    List<Notification> findByUserId(Long userId);
}
