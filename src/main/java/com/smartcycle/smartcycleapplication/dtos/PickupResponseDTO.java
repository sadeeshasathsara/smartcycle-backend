package com.smartcycle.smartcycleapplication.dtos;

import com.smartcycle.smartcycleapplication.models.Status;
import com.smartcycle.smartcycleapplication.models.WasteType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PickupResponseDTO {
    private Long requestId;
    private String residentName;
    private WasteType wasteType;
    private double quantity;
    // CHANGE: Type is now the Status enum
    private Status status;
    private LocalDateTime scheduledDate;
    private double estimatedFee;
}