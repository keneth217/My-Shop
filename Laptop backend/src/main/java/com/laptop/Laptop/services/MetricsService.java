package com.laptop.Laptop.services;

import com.laptop.Laptop.dto.DetailedMetric;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MetricsService {

    @Autowired
    private MeterRegistry meterRegistry;

    // Example of storing metrics in memory; replace with actual metric retrieval logic
    private List<DetailedMetric> metrics = new ArrayList<>();

    public void addMetric(String tenantId, String metricName, double value, String unit) {
        DetailedMetric metric = new DetailedMetric(tenantId, LocalDateTime.now(), metricName, value, unit);
        metrics.add(metric);
    }

    public List<DetailedMetric> getMetrics(String tenantId) {
        // Filter metrics based on tenantId
        if (tenantId != null) {
            return metrics.stream()
                    .filter(metric -> metric.getTenantId().equals(tenantId))
                    .toList();
        }
        return metrics;
    }
}
