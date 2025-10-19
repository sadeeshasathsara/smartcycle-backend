package com.smartcycle.smartcycleapplication.controllers;

import com.smartcycle.smartcycleapplication.dtos.*;
import com.smartcycle.smartcycleapplication.services.CollectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/collections")
public class CollectionController {

    private final CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @PostMapping("/request-pickup")
    public ResponseEntity<Map<String, Object>> requestPickup(
            @RequestBody PickupRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        // The business logic is now called directly. The global handler will catch any exceptions.
        String userEmail = userDetails.getUsername();
        PickupResponseDTO confirmation = collectionService.createPickupRequest(requestDTO, userEmail);

        Map<String, Object> response = Map.of(
                "success", true,
                "message", "Pickup successfully scheduled.",
                "data", confirmation
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/my-requests")
    public ResponseEntity<List<CollectionHistoryDTO>> getMyRequests(@AuthenticationPrincipal UserDetails userDetails) {
        // The success case returns the list directly. The global handler will catch errors.
        String userEmail = userDetails.getUsername();
        List<CollectionHistoryDTO> history = collectionService.getAllRequestsForUser(userEmail);
        return ResponseEntity.ok(history);
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal UserDetails userDetails) {

        // Business logic is called directly. Exceptions are handled globally.
        String userEmail = userDetails.getUsername();
        CollectionHistoryDTO cancelledRequest = collectionService.cancelRequest(requestId, userEmail);

        Map<String, Object> response = Map.of(
                "success", true,
                "message", "Request successfully cancelled.",
                "data", cancelledRequest
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/active")
    // CHANGE: Return type is now ResponseEntity<List<ActiveRequestDTO>>
    public ResponseEntity<List<ActiveRequestDTO>> getActiveRequestStatus(@AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        // CHANGE: Call the renamed service method which returns a List
        List<ActiveRequestDTO> activeRequests = collectionService.getActiveRequestsForUser(userEmail);

        // Return the list directly (it will be empty if none are found, which is fine)
        return ResponseEntity.ok(activeRequests);
    }

    @PatchMapping("/{requestId}/complete")
    public ResponseEntity<?> completePickup(
            @PathVariable Long requestId,
            @RequestBody CompleteRequestDTO completeRequestDTO) {

        CollectionHistoryDTO completedRequest = collectionService.completePickup(requestId, completeRequestDTO);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Pickup successfully completed.",
                        "data", completedRequest
                )
        );
    }
}