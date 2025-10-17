package com.smartcycle.smartcycleapplication.service;

import com.smartcycle.smartcycleapplication.enums.WasteType;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class FeeService {
    public BigDecimal calculateFee(WasteType wasteType, boolean offPeak) {
        BigDecimal base;
        switch (wasteType) {
            case GENERAL -> base = new BigDecimal("5.00");
            case RECYCLABLE -> base = new BigDecimal("2.00");
            case ORGANIC -> base = new BigDecimal("4.00");
            case HAZARDOUS -> base = new BigDecimal("15.00");
            case ELECTRONIC -> base = new BigDecimal("8.00");
            default -> base = new BigDecimal("5.00");
        }
        if (!offPeak) {
            base = base.multiply(new BigDecimal("1.20"));
        }
        return base;
    }
}


