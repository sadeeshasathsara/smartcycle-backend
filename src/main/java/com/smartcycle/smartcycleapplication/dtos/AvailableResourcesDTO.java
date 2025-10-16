package com.smartcycle.smartcycleapplication.dtos;

import lombok.Data;
import java.util.List;

@Data
public class AvailableResourcesDTO {
    private List<AvailableDriverDTO> availableDrivers;
    private List<AvailableVehicleDTO> availableVehicles;
}