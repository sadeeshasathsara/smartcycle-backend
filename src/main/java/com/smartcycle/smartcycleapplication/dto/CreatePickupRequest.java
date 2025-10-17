package com.smartcycle.smartcycleapplication.dto;

import com.smartcycle.smartcycleapplication.enums.WasteType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class CreatePickupRequest {

    @NotNull
    private WasteType wasteType;

    @NotNull
    @Future
    private LocalDateTime preferredDateTime;

    @NotBlank
    @Size(max = 256)
    private String address;

    @Size(max = 1024)
    private String notes;

    public WasteType getWasteType() { return wasteType; }
    public void setWasteType(WasteType wasteType) { this.wasteType = wasteType; }
    public LocalDateTime getPreferredDateTime() { return preferredDateTime; }
    public void setPreferredDateTime(LocalDateTime preferredDateTime) { this.preferredDateTime = preferredDateTime; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}


