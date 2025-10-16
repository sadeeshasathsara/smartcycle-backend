package com.smartcycle.smartcycleapplication.models;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "hazardous_waste_requests")
@Data
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "collection_request_id")
public class HazardousWasteRequest extends CollectionRequest {

    private String hazardLevel;
}