package com.smartcycle.smartcycleapplication.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Entity
@Table(name = "admins")
@Data
@EqualsAndHashCode(callSuper = true)
public class Admin extends User {

    @OneToMany(mappedBy = "generatedByAdmin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Report> reports;
}