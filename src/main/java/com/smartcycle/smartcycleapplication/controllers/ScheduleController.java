package com.smartcycle.smartcycleapplication.controllers;

import com.smartcycle.smartcycleapplication.dtos.CollectionHistoryDTO;
import com.smartcycle.smartcycleapplication.dtos.CreateScheduleRequestDTO;
import com.smartcycle.smartcycleapplication.dtos.ScheduleListDTO;
import com.smartcycle.smartcycleapplication.dtos.ScheduleStartResponseDTO;
import com.smartcycle.smartcycleapplication.models.CollectionSchedule;
import com.smartcycle.smartcycleapplication.services.ScheduleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PatchMapping("/{scheduleId}/start")
    public ResponseEntity<?> startSchedule(@PathVariable Long scheduleId) {
        ScheduleStartResponseDTO response = scheduleService.startSchedule(scheduleId);
        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "data", response
                )
        );
    }

    @PostMapping
    public ResponseEntity<?> createSchedule(
            @RequestBody CreateScheduleRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        String personnelEmail = userDetails.getUsername();
        CollectionSchedule newSchedule = scheduleService.createSchedule(requestDTO, personnelEmail);

        return new ResponseEntity<>(
                Map.of(
                        "success", true,
                        "message", "New collection schedule created successfully.",
                        "data", newSchedule
                ),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<ScheduleListDTO>> getAllSchedules() {
        List<ScheduleListDTO> schedules = scheduleService.getAllSchedules();
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/{scheduleId}/requests")
    public ResponseEntity<List<CollectionHistoryDTO>> getRequestsForSchedule(@PathVariable Long scheduleId) {
        List<CollectionHistoryDTO> requests = scheduleService.getRequestsForSchedule(scheduleId);
        return ResponseEntity.ok(requests);
    }
}