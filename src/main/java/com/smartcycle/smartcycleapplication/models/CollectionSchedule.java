package com.smartcycle.smartcycleapplication.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List; // Import List

@Entity
@Table(name = "collection_schedules")
@Data
public class CollectionSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime scheduledTime;
    @Enumerated(EnumType.STRING)
    private ScheduleStatus status;

    // CHANGE: A schedule now holds a list of requests.
    @OneToMany(mappedBy = "collectionSchedule", cascade = CascadeType.ALL)
    private List<CollectionRequest> collectionRequests;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;

    // See next point for this addition
    @ManyToOne
    @JoinColumn(name = "personnel_id", nullable = false)
    private CollectionPersonnel createdBy;
}