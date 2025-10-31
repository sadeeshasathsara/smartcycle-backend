package com.smartcycle.smartcycleapplication.dtos;

import lombok.Data;
import java.util.List;

@Data
public class OutstandingBalanceDTO {
    private double totalOutstanding;
    private List<CollectionHistoryDTO> unpaidRequests;
}