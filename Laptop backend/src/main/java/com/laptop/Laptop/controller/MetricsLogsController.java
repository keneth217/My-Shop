package com.laptop.Laptop.controller;

import com.laptop.Laptop.dto.DetailedLog;
import com.laptop.Laptop.dto.DetailedMetric;
import com.laptop.Laptop.services.LoggingService;
import com.laptop.Laptop.services.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MetricsLogsController {

    @Autowired
    private MetricsService metricsService;

    @Autowired
    private LoggingService loggingService;

    // API to get metrics
    @GetMapping("/api/metrics")
    public List<DetailedMetric> getMetrics(@RequestParam(required = false) String tenantId) {
        return metricsService.getMetrics(tenantId);
    }

    // API to get logs
    @GetMapping("/api/logs")
    public List<DetailedLog> getLogs(@RequestParam(required = false) String tenantId) {
        return loggingService.getLogs(tenantId);
    }
}
