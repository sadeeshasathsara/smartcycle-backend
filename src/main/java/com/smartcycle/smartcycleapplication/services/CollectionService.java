package com.smartcycle.smartcycleapplication.services;

import com.smartcycle.smartcycleapplication.dtos.*;
import com.smartcycle.smartcycleapplication.models.*;
import com.smartcycle.smartcycleapplication.repositories.CollectionRequestRepository;
import com.smartcycle.smartcycleapplication.repositories.PaymentRepository;
import com.smartcycle.smartcycleapplication.repositories.ResidentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CollectionService {

    private final ResidentRepository residentRepository;
    private final CollectionRequestRepository collectionRequestRepository;
    private final PaymentRepository paymentRepository;

    public CollectionService(ResidentRepository residentRepository,
                             CollectionRequestRepository collectionRequestRepository,
                             PaymentRepository paymentRepository) {
        this.residentRepository = residentRepository;
        this.collectionRequestRepository = collectionRequestRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional(readOnly = true) // This should be @Transactional(readOnly = true)
    public List<CollectionHistoryDTO> getAllRequestsForUser(String userEmail) {
        Resident resident = residentRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("Resident not found for the logged-in user."));

        List<CollectionRequest> requests = collectionRequestRepository.findByResidentIdOrderByScheduleDateDesc(resident.getId());

        return requests.stream()
                .map(this::mapToCollectionHistoryDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PickupResponseDTO createPickupRequest(PickupRequestDTO requestDTO, String userEmail) {
        Resident resident = residentRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("Resident not found for the logged-in user."));

        if (!isSlotAvailable(requestDTO.getPreferredPickupDate())) {
            throw new IllegalArgumentException("No available collection slots for the selected date/time.");
        }

        double fee = calculatePickupFee(requestDTO.getWasteType(), requestDTO.getQuantity());
        Payment newPayment = new Payment();
        newPayment.setAmount(fee);
        newPayment.setDate(LocalDateTime.now());
        // FIX: Use the PaymentStatus enum instead of a String
        newPayment.setStatus(PaymentStatus.PENDING);

        CollectionRequest newRequest = new CollectionRequest();
        newRequest.setResident(resident);
        newRequest.setWasteType(requestDTO.getWasteType());
        newRequest.setQuantity(requestDTO.getQuantity());
        newRequest.setScheduleDate(requestDTO.getPreferredPickupDate());
        newRequest.setStatus(Status.SCHEDULED);
        newRequest.setPriority("Normal");
        newRequest.setPayment(newPayment);
        newPayment.setCollectionRequest(newRequest);

        CollectionRequest savedRequest = collectionRequestRepository.save(newRequest);

        return mapToPickupResponseDTO(savedRequest);
    }

    @Transactional
    public CollectionHistoryDTO cancelRequest(Long requestId, String userEmail) {
        Resident resident = residentRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("Resident not found for the logged-in user."));

        CollectionRequest request = collectionRequestRepository.findByIdAndResidentId(requestId, resident.getId())
                .orElseThrow(() -> new IllegalArgumentException("Request not found or you do not have permission to cancel it."));

        if (request.getStatus() != Status.SCHEDULED) {
            throw new IllegalStateException("This request cannot be cancelled as it is already " + request.getStatus().toString().toLowerCase() + ".");
        }

        request.setStatus(Status.CANCELLED);

        if(request.getPayment() != null) {
            // FIX: Use the PaymentStatus enum instead of a String
            request.getPayment().setStatus(PaymentStatus.CANCELLED);
        }

        CollectionRequest updatedRequest = collectionRequestRepository.save(request);

        return mapToCollectionHistoryDTO(updatedRequest);
    }

    // --- Helper DTO Mappers and other methods ---

    private CollectionHistoryDTO mapToCollectionHistoryDTO(CollectionRequest request) {
        CollectionHistoryDTO dto = new CollectionHistoryDTO();
        dto.setRequestId(request.getId());
        dto.setWasteType(request.getWasteType());
        dto.setQuantity(request.getQuantity());
        dto.setStatus(request.getStatus());
        dto.setScheduledDate(request.getScheduleDate());
        return dto;
    }

    private boolean isSlotAvailable(LocalDateTime dateTime) {
        return true;
    }

    private double calculatePickupFee(WasteType wasteType, double quantity) {
        return switch (wasteType) {
            case BULK_ITEMS, HAZARDOUS -> 25.0 * quantity;
            case E_WASTE -> 15.0;
            default -> 10.0;
        };
    }

    private PickupResponseDTO mapToPickupResponseDTO(CollectionRequest request) {
        PickupResponseDTO dto = new PickupResponseDTO();
        dto.setRequestId(request.getId());
        dto.setResidentName(request.getResident().getName());
        dto.setWasteType(request.getWasteType());
        dto.setQuantity(request.getQuantity());
        dto.setStatus(request.getStatus());
        dto.setScheduledDate(request.getScheduleDate());
        dto.setEstimatedFee(request.getPayment().getAmount());
        return dto;
    }

    @Transactional(readOnly = true)
    public Optional<ActiveRequestDTO> getActiveRequestForUser(String userEmail) {
        Resident resident = residentRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("Resident not found for the logged-in user."));

        // Define the list of "active" statuses
        List<Status> activeStatuses = List.of(Status.SCHEDULED, Status.ASSIGNED, Status.EN_ROUTE);

        // Find the next upcoming active request
        return collectionRequestRepository
                .findFirstByResidentIdAndStatusInOrderByScheduleDateAsc(resident.getId(), activeStatuses)
                .map(this::mapToActiveRequestDTO); // Convert to DTO if found
    }

    private ActiveRequestDTO mapToActiveRequestDTO(CollectionRequest request) {
        ActiveRequestDTO dto = new ActiveRequestDTO();
        dto.setRequestId(request.getId());
        dto.setWasteType(request.getWasteType());
        dto.setStatus(request.getStatus());
        dto.setScheduledDate(request.getScheduleDate());
        return dto;
    }

    @Transactional
    public CollectionHistoryDTO completePickup(Long requestId, CompleteRequestDTO completeRequestDTO) {
        // 1. Find the request or throw an error
        CollectionRequest request = collectionRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Collection request with ID " + requestId + " not found."));

        // 2. Validate that the request is in the correct state to be completed
        if (request.getStatus() != Status.EN_ROUTE) {
            throw new IllegalStateException("This request cannot be completed as its status is " + request.getStatus() + ", not EN_ROUTE.");
        }

        // 3. Simulate bin tag validation
        if (!isValidBinTag(completeRequestDTO.getBinTagId(), request.getResident())) {
            throw new IllegalArgumentException("Invalid or unassigned bin tag: " + completeRequestDTO.getBinTagId());
        }

        // 4. Update the request details
        request.setStatus(Status.COMPLETED);
        // Optionally, update the quantity with the actual measured weight
        request.setQuantity(completeRequestDTO.getActualWeight());

        CollectionRequest updatedRequest = collectionRequestRepository.save(request);

        // 5. Notify the resident
        // TODO: Implement notification logic to inform the resident of the completed pickup.
        // notificationService.notifyResidentOfCompletion(updatedRequest.getResident());

        // 6. Return a confirmation DTO
        return mapToCollectionHistoryDTO(updatedRequest);
    }

    // --- Helper Method for Bin Tag Validation ---
    private boolean isValidBinTag(String binTagId, Resident resident) {
        // TODO: Implement real validation logic.
        // This would query the WasteBin repository to check if the binTagId exists
        // and is assigned to the correct resident.
        if (binTagId == null || binTagId.isBlank()) {
            return false;
        }
        return true; // For now, we'll assume any non-empty tag is valid.
    }
}