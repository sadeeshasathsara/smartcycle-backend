package com.smartcycle.smartcycleapplication.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "routes")
@Data
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String routeId;
    private String name;
    private double distance;

    // A route can have multiple schedule entries
    @OneToMany(mappedBy = "route")
    private List<CollectionSchedule> schedules;
}
