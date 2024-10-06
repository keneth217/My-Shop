package com.laptop.Laptop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor  // Add this
@AllArgsConstructor //
@Entity

public class TenantConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long shopId; // This could also be a foreign key to a Shop entity
    private String theme;
    private String logoUrl;
    private boolean featureXEnabled; // Example feature toggle
    // Add other configuration fields as needed

    // Getters and Setters
    // ...
}
