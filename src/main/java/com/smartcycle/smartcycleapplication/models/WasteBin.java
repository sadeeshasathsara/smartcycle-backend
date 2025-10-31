package com.smartcycle.smartcycleapplication.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "waste_bins")
@Data
public class WasteBin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String binId;
    private String type; // e.g., General, Recycling, Organic
    private double capacity;
    private double currentLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id")
    private Resident resident;
}