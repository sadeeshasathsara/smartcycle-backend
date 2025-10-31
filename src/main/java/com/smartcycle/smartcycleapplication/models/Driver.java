package com.smartcycle.smartcycleapplication.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "drivers")
@Data
@EqualsAndHashCode(callSuper = true)
public class Driver extends User {

    // Driver-specific attributes can go here if any
}