package com.smartcycle.smartcycleapplication.services;

import com.smartcycle.smartcycleapplication.dtos.AvailableVehicleDTO;
import com.smartcycle.smartcycleapplication.dtos.CreateVehicleDTO;
import com.smartcycle.smartcycleapplication.models.Vehicle;
import com.smartcycle.smartcycleapplication.repositories.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    // --- NEW METHOD: Get a list of all vehicles ---
    @Transactional(readOnly = true)
    public List<AvailableVehicleDTO> getAllVehicles() {
        return vehicleRepository.findAll()
                .stream()
                .map(this::mapToVehicleDTO)
                .collect(Collectors.toList());
    }

    // --- Existing createVehicle method ---
    @Transactional
    public Vehicle createVehicle(CreateVehicleDTO dto) {
        vehicleRepository.findByVehicleId(dto.getVehicleId()).ifPresent(v -> {
            throw new IllegalStateException("A vehicle with ID '" + dto.getVehicleId() + "' already exists.");
        });

        Vehicle newVehicle = new Vehicle();
        newVehicle.setVehicleId(dto.getVehicleId());
        newVehicle.setType(dto.getType());
        newVehicle.setCapacity(dto.getCapacity());
        newVehicle.setCurrentLocation("Depot");

        return vehicleRepository.save(newVehicle);
    }

    // --- Helper DTO Mapper ---
    private AvailableVehicleDTO mapToVehicleDTO(Vehicle vehicle) {
        AvailableVehicleDTO dto = new AvailableVehicleDTO();
        dto.setId(vehicle.getId());
        dto.setVehicleId(vehicle.getVehicleId());
        dto.setType(vehicle.getType());
        dto.setCapacity(vehicle.getCapacity());
        return dto;
    }
}