package com.laptop.Laptop.services;


import com.laptop.Laptop.dto.AuditTrailDTO;
import com.laptop.Laptop.entity.AuditTrail;
import com.laptop.Laptop.repository.AuditTrailRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private final AuditTrailRepository auditTrailRepository;

    public AuditLogService(AuditTrailRepository auditTrailRepository) {
        this.auditTrailRepository = auditTrailRepository;
    }

    public void save(AuditTrailDTO auditTrailDTO){
        auditTrailRepository.save(new AuditTrail(auditTrailDTO));
    }

}
