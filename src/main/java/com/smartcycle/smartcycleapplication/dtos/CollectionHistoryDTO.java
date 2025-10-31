package com.smartcycle.smartcycleapplication.dtos;

import com.smartcycle.smartcycleapplication.models.Status;
import com.smartcycle.smartcycleapplication.models.WasteType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CollectionHistoryDTO {
    private Long requestId;
    private WasteType wasteType;
    private double quantity;
    private Status status;
    private LocalDateTime scheduledDate;
}