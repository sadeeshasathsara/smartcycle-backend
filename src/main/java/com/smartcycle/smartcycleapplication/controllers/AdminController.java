package com.smartcycle.smartcycleapplication.controllers;

import com.smartcycle.smartcycleapplication.dtos.AvailableVehicleDTO;
import com.smartcycle.smartcycleapplication.dtos.CreateVehicleDTO;
import com.smartcycle.smartcycleapplication.models.Vehicle;
import com.smartcycle.smartcycleapplication.services.VehicleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final VehicleService vehicleService;

    public AdminController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    // --- NEW ENDPOINT: Get a list of all vehicles ---
    @GetMapping("/vehicles")
    public ResponseEntity<List<AvailableVehicleDTO>> getAllVehicles() {
        List<AvailableVehicleDTO> vehicles = vehicleService.getAllVehicles();
        return ResponseEntity.ok(vehicles);
    }

    // --- Existing POST /vehicles endpoint ---
    @PostMapping("/vehicles")
    public ResponseEntity<?> addVehicle(@RequestBody CreateVehicleDTO vehicleDTO) {
        Vehicle newVehicle = vehicleService.createVehicle(vehicleDTO);
        return new ResponseEntity<>(
                Map.of(
                        "success", true,
                        "message", "Vehicle added successfully.",
                        "data", newVehicle
                ),
                HttpStatus.CREATED
        );
    }
}