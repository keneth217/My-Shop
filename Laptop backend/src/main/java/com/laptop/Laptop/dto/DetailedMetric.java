package com.laptop.Laptop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailedMetric {
    private String tenantId;
    private LocalDateTime timestamp;
    private String metricName;
    private double value;
    private String unit; // e.g., seconds, requests, etc.
}