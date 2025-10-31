package com.smartcycle.smartcycleapplication.services;

import com.smartcycle.smartcycleapplication.dtos.CollectionHistoryDTO;
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
        CollectionPersonnel personnel = personnelRepository.findByEmail(personnelEmail)
                .orElseThrow(() -> new IllegalStateException("Collection Personnel not found."));
        Driver driver = driverRepository.findById(dto.getDriverId())
                .orElseThrow(() -> new IllegalArgumentException("Driver not found."));
        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found."));

        validateResourceAvailability(dto.getScheduledDateTime(), dto.getDriverId(), dto.getVehicleId());

        CollectionSchedule newSchedule = new CollectionSchedule();
        newSchedule.setCreatedBy(personnel);
        newSchedule.setDriver(driver);
        newSchedule.setVehicle(vehicle);
        newSchedule.setScheduledTime(dto.getScheduledDateTime());
        newSchedule.setStatus(ScheduleStatus.PENDING);
        // Location would typically be derived from the assigned requests or a defined route

        // Save the schedule *first* to get its ID
        CollectionSchedule savedSchedule = scheduleRepository.save(newSchedule);

        // --- NEW: Assign matching requests ---
        assignMatchingRequestsToSchedule(savedSchedule);
        // --- End of New Logic ---

        // Return the schedule (now potentially updated with requests, though the list might not be immediately populated depending on JPA state)
        return savedSchedule; // Or fetch it again if needed: scheduleRepository.findById(savedSchedule.getId()).get()
    }

    // --- NEW HELPER METHOD ---
    private void assignMatchingRequestsToSchedule(CollectionSchedule schedule) {
        // Define the time window for matching requests (e.g., +/- 2 hours around the schedule time)
        LocalDateTime startTime = schedule.getScheduledTime().minusHours(2);
        LocalDateTime endTime = schedule.getScheduledTime().plusHours(2);

        // Find SCHEDULED requests within the time window that haven't been assigned yet
        List<CollectionRequest> matchingRequests = requestRepository.findByStatusAndScheduleDateBetweenAndCollectionScheduleIsNull(
                Status.SCHEDULED,
                startTime,
                endTime
        );

        // TODO: Add location filtering here if necessary. This is complex and might involve
        // comparing addresses, zip codes, or using geospatial queries if you store coordinates.

        // Assign the found requests to the new schedule and update their status
        for (CollectionRequest request : matchingRequests) {
            request.setCollectionSchedule(schedule);
            request.setStatus(Status.ASSIGNED);
        }

        // Save the updated requests
        if (!matchingRequests.isEmpty()) {
            requestRepository.saveAll(matchingRequests);
        }
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

    @Transactional(readOnly = true)
    public List<CollectionHistoryDTO> getRequestsForSchedule(Long scheduleId) {
        // 1. Check if the schedule exists (optional but good practice)
        scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule with ID " + scheduleId + " not found."));

        // 2. Fetch the requests linked to this schedule
        List<CollectionRequest> requests = requestRepository.findByCollectionScheduleId(scheduleId);

        // 3. Map them to the DTO for the response
        return requests.stream()
                .map(this::mapToCollectionHistoryDTO) // Use the existing mapper
                .collect(Collectors.toList());
    }

    private CollectionHistoryDTO mapToCollectionHistoryDTO(CollectionRequest request) {
        CollectionHistoryDTO dto = new CollectionHistoryDTO();
        dto.setRequestId(request.getId());
        dto.setWasteType(request.getWasteType());
        dto.setQuantity(request.getQuantity());
        dto.setStatus(request.getStatus());
        dto.setScheduledDate(request.getScheduledDate());
        return dto;
    }

    private CollectionHistoryDTO mapRequestToHistoryDTO(CollectionRequest request) {
        CollectionHistoryDTO dto = new CollectionHistoryDTO();
        // ... map fields ...
        return dto;
    }

}