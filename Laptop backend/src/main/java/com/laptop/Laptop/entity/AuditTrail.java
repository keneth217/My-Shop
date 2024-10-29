package com.laptop.Laptop.entity;

import com.laptop.Laptop.dto.AuditTrailDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.InetAddress;
import java.net.UnknownHostException;
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
    private String className;          // Entity class name
    private String persistedObjectId;  // ID of the affected entity
    private String eventName;          // INSERT, UPDATE, DELETE
    private String propertyName;       // Changed field/property
    private String oldValue;
    private String newValue;

    private String modifiedBy;         // User responsible for the change
    private String computerName;       // System/computer name
    private String computerAddress;    // IP address of the system

    public AuditTrail(String className, String persistedObjectId, String eventName, String propertyName, String oldValue, String newValue, String modifiedBy, String computerName, String computerAddress) {
    }


    @PrePersist
    void beforeInsert() {
        this.dateCreated = LocalDate.now();
        this.lastUpdated = LocalDate.now();
        captureSystemInfo();
    }

    @PreUpdate
    void beforeUpdate() {
        this.lastUpdated = LocalDate.now();
        captureSystemInfo();
    }

    public AuditTrail(AuditTrailDTO auditTrailDTO) {
        this.className = auditTrailDTO.getClassName();
        this.persistedObjectId = auditTrailDTO.getPersistedObjectId();
        this.eventName = auditTrailDTO.getEventName();
        this.propertyName = auditTrailDTO.getPropertyName();
        this.oldValue = auditTrailDTO.getOldValue();
        this.newValue = auditTrailDTO.getNewValue();
        this.modifiedBy = auditTrailDTO.getModifiedBy();  // Set modifiedBy field
    }

    private void captureSystemInfo() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            this.computerName = localHost.getHostName();
            this.computerAddress = localHost.getHostAddress();
        } catch (UnknownHostException e) {
            this.computerName = "UNKNOWN";
            this.computerAddress = "UNKNOWN";
        }
    }
}