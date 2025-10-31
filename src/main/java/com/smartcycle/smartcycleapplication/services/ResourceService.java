package com.smartcycle.smartcycleapplication.services;

import com.smartcycle.smartcycleapplication.dtos.AvailableDriverDTO;
import com.smartcycle.smartcycleapplication.dtos.AvailableResourcesDTO;
import com.smartcycle.smartcycleapplication.dtos.AvailableVehicleDTO;
import com.smartcycle.smartcycleapplication.models.Driver;
import com.smartcycle.smartcycleapplication.models.Vehicle;
import com.smartcycle.smartcycleapplication.repositories.CollectionScheduleRepository;
import com.smartcycle.smartcycleapplication.repositories.DriverRepository;
import com.smartcycle.smartcycleapplication.repositories.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourceService {

    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final CollectionScheduleRepository scheduleRepository;

    public ResourceService(DriverRepository driverRepository, VehicleRepository vehicleRepository, CollectionScheduleRepository scheduleRepository) {
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Transactional(readOnly = true)
    public AvailableResourcesDTO getAvailableResources(LocalDateTime dateTime) {
        // Define a time window for the slot, e.g., +/- 2 hours to prevent overlaps.
        LocalDateTime startTime = dateTime.minusHours(2);
        LocalDateTime endTime = dateTime.plusHours(2);

        // 1. Get the IDs of all resources that are already booked in this time slot.
        List<Long> bookedDriverIds = scheduleRepository.findBookedDriverIdsByTimeRange(startTime, endTime);
        List<Long> bookedVehicleIds = scheduleRepository.findBookedVehicleIdsByTimeRange(startTime, endTime);

        // 2. Fetch all drivers and filter out the ones that are booked.
        List<AvailableDriverDTO> availableDrivers = driverRepository.findAll().stream()
                .filter(driver -> !bookedDriverIds.contains(driver.getId()))
                .map(this::mapToDriverDTO)
                .collect(Collectors.toList());

        // 3. Fetch all vehicles and filter out the ones that are booked.
        List<AvailableVehicleDTO> availableVehicles = vehicleRepository.findAll().stream()
                .filter(vehicle -> !bookedVehicleIds.contains(vehicle.getId()))
                .map(this::mapToVehicleDTO)
                .collect(Collectors.toList());

        // 4. Package the results into the response DTO.
        AvailableResourcesDTO resourcesDTO = new AvailableResourcesDTO();
        resourcesDTO.setAvailableDrivers(availableDrivers);
        resourcesDTO.setAvailableVehicles(availableVehicles);
        return resourcesDTO;
    }

    // --- Helper DTO Mappers ---
    private AvailableDriverDTO mapToDriverDTO(Driver driver) {
        AvailableDriverDTO dto = new AvailableDriverDTO();
        dto.setId(driver.getId());
        dto.setName(driver.getName());
        return dto;
    }

    private AvailableVehicleDTO mapToVehicleDTO(Vehicle vehicle) {
        AvailableVehicleDTO dto = new AvailableVehicleDTO();
        dto.setId(vehicle.getId());
        dto.setVehicleId(vehicle.getVehicleId());
        dto.setType(vehicle.getType());
        dto.setCapacity(vehicle.getCapacity());
        return dto;
    }
}