package com.smartcycle.smartcycleapplication.dtos;

import com.smartcycle.smartcycleapplication.models.ScheduleStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor // A handy Lombok annotation for a constructor with all fields
public class ScheduleStartResponseDTO {
    private Long scheduleId;
    private ScheduleStatus newStatus;
    private int updatedRequestsCount;
    private String message;
}