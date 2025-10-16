package com.smartcycle.smartcycleapplication.dtos;

import com.smartcycle.smartcycleapplication.models.WasteType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateScheduleRequestDTO {
    private WasteType collectionType; // Using WasteType enum for consistency
    private String location;
    private LocalDateTime scheduledDateTime;
    private Long driverId;
    private Long vehicleId;
    private String notes;
    private BulkDetailsDTO bulkDetails; // Optional, only for BULK_ITEMS
}