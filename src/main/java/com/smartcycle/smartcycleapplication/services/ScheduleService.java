package com.smartcycle.smartcycleapplication.services;

import com.smartcycle.smartcycleapplication.dtos.CreateScheduleRequestDTO;
import com.smartcycle.smartcycleapplication.dtos.ScheduleListDTO;
import com.smartcycle.smartcycleapplication.dtos.ScheduleStartResponseDTO;
import com.smartcycle.smartcycleapplication.models.*;
import com.smartcycle.smartcycleapplication.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    private final CollectionScheduleRepository scheduleRepository;
    private final CollectionRequestRepository requestRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final CollectionPersonnelRepository personnelRepository;

    public ScheduleService(CollectionScheduleRepository scheduleRepository, CollectionRequestRepository requestRepository, DriverRepository driverRepository, VehicleRepository vehicleRepository, CollectionPersonnelRepository personnelRepository) {
        this.scheduleRepository = scheduleRepository;
        this.requestRepository = requestRepository;
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
        this.personnelRepository = personnelRepository;
    }

    @Transactional
    public ScheduleStartResponseDTO startSchedule(Long scheduleId) {
        // 1. Find the schedule or throw an error
        CollectionSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule with ID " + scheduleId + " not found."));

        // 2. Validate that the schedule can be started
        if (schedule.getStatus() != ScheduleStatus.PENDING) {
            throw new IllegalStateException("This schedule cannot be started as it is already " + schedule.getStatus().toString().toLowerCase() + ".");
        }

        // 3. Find all associated collection requests
        List<CollectionRequest> requestsToUpdate = requestRepository.findByCollectionScheduleId(scheduleId);

        // 4. Update the status of each request to EN_ROUTE
        for (CollectionRequest request : requestsToUpdate) {
            request.setStatus(Status.EN_ROUTE);
        }
        requestRepository.saveAll(requestsToUpdate);

        // 5. Update the schedule's status to IN_PROGRESS
        schedule.setStatus(ScheduleStatus.IN_PROGRESS);
        scheduleRepository.save(schedule);

        // 6. Return a confirmation response
        return new ScheduleStartResponseDTO(
                schedule.getId(),
                schedule.getStatus(),
                requestsToUpdate.size(),
                "Schedule has been started and " + requestsToUpdate.size() + " requests are now EN_ROUTE."
        );
    }

    @Transactional
    public CollectionSchedule createSchedule(CreateScheduleRequestDTO dto, String personnelEmail) {
        // 1. Find the personnel creating the schedule
        CollectionPersonnel personnel = personnelRepository.findByEmail(personnelEmail)
                .orElseThrow(() -> new IllegalStateException("Collection Personnel not found."));

        // 2. Find the assigned driver and vehicle
        Driver driver = driverRepository.findById(dto.getDriverId())
                .orElseThrow(() -> new IllegalArgumentException("Driver with ID " + dto.getDriverId() + " not found."));
        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle with ID " + dto.getVehicleId() + " not found."));

        // 3. Final validation: Check for resource availability to prevent race conditions
        validateResourceAvailability(dto.getScheduledDateTime(), dto.getDriverId(), dto.getVehicleId());

        // 4. Create and populate the new schedule entity
        CollectionSchedule newSchedule = new CollectionSchedule();
        newSchedule.setCreatedBy(personnel);
        newSchedule.setDriver(driver);
        newSchedule.setVehicle(vehicle);
        newSchedule.setScheduledTime(dto.getScheduledDateTime());
        newSchedule.setStatus(ScheduleStatus.PENDING);
        // Note: For this simple schedule, we are not linking individual requests yet.
        // A more complex system might link existing 'SCHEDULED' requests here.

        return scheduleRepository.save(newSchedule);
    }

    private void validateResourceAvailability(LocalDateTime dateTime, Long driverId, Long vehicleId) {
        LocalDateTime startTime = dateTime.minusHours(2);
        LocalDateTime endTime = dateTime.plusHours(2);

        List<Long> bookedDrivers = scheduleRepository.findBookedDriverIdsByTimeRange(startTime, endTime);
        if (bookedDrivers.contains(driverId)) {
            throw new IllegalStateException("Driver is already booked for this time slot.");
        }

        List<Long> bookedVehicles = scheduleRepository.findBookedVehicleIdsByTimeRange(startTime, endTime);
        if (bookedVehicles.contains(vehicleId)) {
            throw new IllegalStateException("Vehicle is already booked for this time slot.");
        }
    }

    @Transactional(readOnly = true)
    public List<ScheduleListDTO> getAllSchedules() {
        return scheduleRepository.findAllByOrderByScheduledTimeDesc()
                .stream()
                .map(this::mapToScheduleListDTO)
                .collect(Collectors.toList());
    }

    // --- Helper DTO Mapper ---
    private ScheduleListDTO mapToScheduleListDTO(CollectionSchedule schedule) {
        ScheduleListDTO dto = new ScheduleListDTO();
        dto.setId(schedule.getId());
        dto.setScheduledTime(schedule.getScheduledTime());
        dto.setStatus(schedule.getStatus());

        // Add null checks for safety
        if (schedule.getDriver() != null) {
            dto.setDriverName(schedule.getDriver().getName());
        }
        if (schedule.getVehicle() != null) {
            dto.setVehicleId(schedule.getVehicle().getVehicleId());
        }
        if (schedule.getCreatedBy() != null) {
            dto.setCreatedBy(schedule.getCreatedBy().getName());
        }

        return dto;
    }

}