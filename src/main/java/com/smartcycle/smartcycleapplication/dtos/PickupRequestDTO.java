package com.smartcycle.smartcycleapplication.dtos;

import com.smartcycle.smartcycleapplication.models.WasteType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PickupRequestDTO {
    private WasteType wasteType;
    private double quantity;
    private LocalDateTime preferredPickupDate;
}