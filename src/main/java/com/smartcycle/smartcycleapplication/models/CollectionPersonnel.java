package com.smartcycle.smartcycleapplication.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "collection_personnel")
@Data
@EqualsAndHashCode(callSuper = true)
public class CollectionPersonnel extends User {

    private String assignedArea;
}