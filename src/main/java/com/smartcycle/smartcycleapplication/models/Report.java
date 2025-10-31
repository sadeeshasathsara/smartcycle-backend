package com.smartcycle.smartcycleapplication.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Data
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reportId;
    private String type; // e.g., Collection, System Health
    private LocalDateTime generatedDate;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin generatedByAdmin;
}
