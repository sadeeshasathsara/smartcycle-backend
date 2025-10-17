package com.smartcycle.smartcycleapplication.service;

import com.smartcycle.smartcycleapplication.repository.PickupRequestRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;

@Service
public class AvailabilityService {

    private final PickupRequestRepository repository;

    private static final int MAX_REQUESTS_PER_HOUR = 10;

    public AvailabilityService(PickupRequestRepository repository) {
        this.repository = repository;
    }

    public boolean isSlotAvailable(LocalDateTime requestedDateTime) {
        LocalDateTime startOfHour = requestedDateTime.truncatedTo(ChronoUnit.HOURS);
        LocalDateTime endOfHour = startOfHour.plusHours(1);
        long count = repository.countByPreferredDateTimeBetween(startOfHour, endOfHour);
        return count < MAX_REQUESTS_PER_HOUR;
    }
}


