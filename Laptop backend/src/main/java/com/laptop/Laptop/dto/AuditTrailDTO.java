package com.laptop.Laptop.dto;

import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuditTrailDTO {
    private String className;
    private String persistedObjectId;
    private String eventName;
    private String propertyName;
    private String oldValue;
    private String newValue;
    private String modifiedBy;         // User responsible for the change
    private String computerName;       // Hostname of the system
    private String computerAddress;    // IP address of the system

    public AuditTrailDTO(String canonicalName, String string, String name, String propertyName, String aNull, String aNull1) {
    }
}
