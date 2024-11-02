package com.laptop.Laptop.dto;

import com.laptop.Laptop.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDto {
    private Long id;
    private String name;
    private double salary;
    private String phoneNumber;
    private Roles role;
    private String employerRole;
    private Long shopId; // Include shop ID if needed for future reference
}