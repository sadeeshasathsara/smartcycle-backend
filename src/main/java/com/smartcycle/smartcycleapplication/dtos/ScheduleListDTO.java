package com.smartcycle.smartcycleapplication.dtos;

import com.smartcycle.smartcycleapplication.models.ScheduleStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ScheduleListDTO {
    private Long id;
    private LocalDateTime scheduledTime;
    private ScheduleStatus status;
    private String driverName;
    private String vehicleId;
    private String createdBy;
}