package com.smartcycle.smartcycleapplication.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Entity
@Table(name = "residents")
@Data
@EqualsAndHashCode(callSuper = true)
public class Resident extends User {

    @Column(nullable = false)
    private String address;

    private double accountBalance;

    @OneToMany(mappedBy = "resident", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CollectionRequest> collectionRequests;

    @OneToMany(mappedBy = "resident", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WasteBin> wasteBins;
}