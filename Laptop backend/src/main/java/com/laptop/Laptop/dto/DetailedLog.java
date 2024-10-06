package com.laptop.Laptop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailedLog {
    private String tenantId;
    private LocalDateTime timestamp;
    private String level;  // e.g., INFO, ERROR, WARN
    private String message;
    private String details; // Additional details about the log
}