package com.smartcycle.smartcycleapplication.dtos;

import lombok.Data;

@Data
public class AvailableVehicleDTO {
    private Long id;
    private String vehicleId;
    private String type;
    private double capacity;
}