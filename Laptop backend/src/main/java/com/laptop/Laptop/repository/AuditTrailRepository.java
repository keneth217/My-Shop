package com.laptop.Laptop.repository;


import com.laptop.Laptop.entity.AuditTrail;
import org.springframework.data.repository.CrudRepository;

public interface AuditTrailRepository extends CrudRepository<AuditTrail, Long> {
}
