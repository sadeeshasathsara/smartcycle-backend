package com.smartcycle.smartcycleapplication.controller;

import com.smartcycle.smartcycleapplication.dto.CreatePickupRequest;
import com.smartcycle.smartcycleapplication.dto.PickupResponse;
import com.smartcycle.smartcycleapplication.service.PickupRequestService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/collections")
public class PickupRequestController {

    private final PickupRequestService pickupRequestService;

    public PickupRequestController(PickupRequestService pickupRequestService) {
        this.pickupRequestService = pickupRequestService;
    }

    @PostMapping("/request-pickup")
    public ResponseEntity<PickupResponse> requestPickup(@Valid @RequestBody CreatePickupRequest request, 
                                                        Authentication authentication) {
        String username = authentication.getName();
        PickupResponse response = pickupRequestService.create(request, username);
        if (response.getId() == null) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
}


