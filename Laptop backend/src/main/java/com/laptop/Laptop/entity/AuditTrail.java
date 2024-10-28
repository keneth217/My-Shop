package com.laptop.Laptop.entity;


import com.laptop.Laptop.dto.AuditTrailDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class AuditTrail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate dateCreated;
    private LocalDate lastUpdated;
    private String className;
    private String persistedObjectId;
    private String eventName;
    private String propertyName;
    private String oldValue;
    private String newValue;
    private String computerName;
    private String computerAddress;

    @PrePersist

    void beforeInsert() {
        this.dateCreated = LocalDate.now();
        this.lastUpdated = LocalDate.now();
    }

    @PreUpdate
    void beforeUpdate() {
        this.lastUpdated = LocalDate.now();
    }

    public AuditTrail(AuditTrailDTO auditTrailDTO) {
        this.className = auditTrailDTO.getClassName();
        this.persistedObjectId = auditTrailDTO.getPersistedObjectId();
        this.eventName = auditTrailDTO.getEventName();
        this.propertyName = auditTrailDTO.getPropertyName();
        this.oldValue = auditTrailDTO.getOldValue();
        this.newValue = auditTrailDTO.getNewValue();
    }

}
