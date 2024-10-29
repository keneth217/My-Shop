package com.laptop.Laptop.services;

import com.laptop.Laptop.dto.AuditTrailDTO;
import com.laptop.Laptop.entity.AuditTrail;
import com.laptop.Laptop.repository.AuditTrailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    @Autowired
    private AuditTrailRepository auditTrailRepository;

    private boolean isLoggingAudit = false;

    public void save(AuditTrailDTO auditTrail) {
        if (isLoggingAudit) return; // Prevent recursive calls

        isLoggingAudit = true;
        try {
            // Convert DTO to entity and save to the database
            AuditTrail entity = new AuditTrail(
                    auditTrail.getClassName(),
                    auditTrail.getPersistedObjectId(),
                    auditTrail.getEventName(),
                    auditTrail.getPropertyName(),
                    auditTrail.getOldValue(),
                    auditTrail.getNewValue(),
                    auditTrail.getModifiedBy(),
                    auditTrail.getComputerName(),
                    auditTrail.getComputerAddress()
            );
            auditTrailRepository.save(entity);
        } finally {
            isLoggingAudit = false; // Reset flag
        }
    }
}