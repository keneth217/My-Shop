package com.laptop.Laptop.services;


import com.laptop.Laptop.dto.DetailedLog;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoggingService {

    // Example of storing logs in memory; replace with actual log retrieval logic
    private List<DetailedLog> logs = new ArrayList<>();

    public void addLog(String tenantId, String level, String message, String details) {
        DetailedLog log = new DetailedLog(tenantId, LocalDateTime.now(), level, message, details);
        logs.add(log);
    }

    public List<DetailedLog> getLogs(String tenantId) {
        // Filter logs based on tenantId
        if (tenantId != null) {
            return logs.stream()
                    .filter(log -> log.getTenantId().equals(tenantId))
                    .toList();
        }
        return logs;
    }
}
