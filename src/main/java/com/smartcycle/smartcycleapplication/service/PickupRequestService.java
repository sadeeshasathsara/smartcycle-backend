package com.smartcycle.smartcycleapplication.service;

import com.smartcycle.smartcycleapplication.dto.CreatePickupRequest;
import com.smartcycle.smartcycleapplication.dto.PickupResponse;
import com.smartcycle.smartcycleapplication.entity.PickupRequest;
import com.smartcycle.smartcycleapplication.enums.PickupStatus;
import com.smartcycle.smartcycleapplication.repository.PickupRequestRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PickupRequestService {

    private final AvailabilityService availabilityService;
    private final FeeService feeService;
    private final PaymentService paymentService;
    private final PickupRequestRepository repository;

    public PickupRequestService(AvailabilityService availabilityService,
                                FeeService feeService,
                                PaymentService paymentService,
                                PickupRequestRepository repository) {
        this.availabilityService = availabilityService;
        this.feeService = feeService;
        this.paymentService = paymentService;
        this.repository = repository;
    }

    @Transactional
    public PickupResponse create(CreatePickupRequest createDto, String username) {
        boolean slotAvailable = availabilityService.isSlotAvailable(createDto.getPreferredDateTime());
        if (!slotAvailable) {
            PickupResponse resp = new PickupResponse();
            resp.setStatus(PickupStatus.REJECTED);
            return resp;
        }

        boolean offPeak = isOffPeak(createDto.getPreferredDateTime());
        var fee = feeService.calculateFee(createDto.getWasteType(), offPeak);

        PickupRequest entity = new PickupRequest();
        entity.setWasteType(createDto.getWasteType());
        entity.setPreferredDateTime(createDto.getPreferredDateTime());
        entity.setAddress(createDto.getAddress());
        entity.setNotes(createDto.getNotes());
        entity.setStatus(PickupStatus.SCHEDULED);
        entity.setFee(fee);
        entity.setCreatedAt(LocalDateTime.now());

        PickupRequest saved = repository.save(entity);

        // Create payment record
        paymentService.createPayment(saved, "CREDIT_CARD");

        PickupResponse resp = new PickupResponse();
        resp.setId(saved.getId());
        resp.setStatus(saved.getStatus());
        resp.setWasteType(saved.getWasteType());
        resp.setPreferredDateTime(saved.getPreferredDateTime());
        resp.setAddress(saved.getAddress());
        resp.setNotes(saved.getNotes());
        resp.setFee(saved.getFee());
        return resp;
    }

    private boolean isOffPeak(LocalDateTime dt) {
        LocalTime t = dt.toLocalTime();
        return t.isBefore(LocalTime.of(8, 0)) || t.isAfter(LocalTime.of(18, 0));
    }
}


