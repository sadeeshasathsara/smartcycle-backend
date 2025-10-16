package com.smartcycle.smartcycleapplication.repositories;

import com.smartcycle.smartcycleapplication.models.CollectionSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CollectionScheduleRepository extends JpaRepository<CollectionSchedule, Long> {
    // Find all schedules assigned to a specific driver
    List<CollectionSchedule> findByDriverId(Long driverId);

    List<CollectionSchedule> findAllByOrderByScheduledTimeDesc();

    @Query("SELECT s.driver.id FROM CollectionSchedule s WHERE s.scheduledTime BETWEEN :startTime AND :endTime AND s.driver IS NOT NULL")
    List<Long> findBookedDriverIdsByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT s.vehicle.id FROM CollectionSchedule s WHERE s.scheduledTime BETWEEN :startTime AND :endTime AND s.vehicle IS NOT NULL")
    List<Long> findBookedVehicleIdsByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
