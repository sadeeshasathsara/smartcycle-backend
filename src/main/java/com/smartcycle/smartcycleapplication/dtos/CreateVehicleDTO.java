package com.smartcycle.smartcycleapplication.dtos;

import lombok.Data;

@Data
public class CreateVehicleDTO {
    private String vehicleId; // e.g., "WP-CAB-1234"
    private String type;      // e.g., "Standard Garbage Truck"
    private double capacity;  // e.g., 25.0
}