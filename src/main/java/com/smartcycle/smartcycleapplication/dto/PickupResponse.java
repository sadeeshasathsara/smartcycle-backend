package com.smartcycle.smartcycleapplication.dto;

import com.smartcycle.smartcycleapplication.enums.PickupStatus;
import com.smartcycle.smartcycleapplication.enums.WasteType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PickupResponse {
    private Long id;
    private PickupStatus status;
    private WasteType wasteType;
    private LocalDateTime preferredDateTime;
    private String address;
    private String notes;
    private BigDecimal fee;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public PickupStatus getStatus() { return status; }
    public void setStatus(PickupStatus status) { this.status = status; }
    public WasteType getWasteType() { return wasteType; }
    public void setWasteType(WasteType wasteType) { this.wasteType = wasteType; }
    public LocalDateTime getPreferredDateTime() { return preferredDateTime; }
    public void setPreferredDateTime(LocalDateTime preferredDateTime) { this.preferredDateTime = preferredDateTime; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }
}


