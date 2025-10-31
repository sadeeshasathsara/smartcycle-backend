package com.smartcycle.smartcycleapplication.dtos;

import com.smartcycle.smartcycleapplication.models.Status;
import com.smartcycle.smartcycleapplication.models.WasteType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ActiveRequestDTO {
    private Long requestId;
    private WasteType wasteType;
    private Status status;
    private LocalDateTime scheduledDate;
    // We can add more fields here later, e.g., driverName, vehiclePlate
}