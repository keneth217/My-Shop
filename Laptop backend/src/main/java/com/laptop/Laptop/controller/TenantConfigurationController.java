package com.laptop.Laptop.controller;

import com.laptop.Laptop.entity.TenantConfiguration;
import com.laptop.Laptop.services.TenantConfigurationService;
import com.laptop.Laptop.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/configurations")
public class TenantConfigurationController {

    @Autowired
    private TenantConfigurationService tenantConfigurationService;

    @Autowired
    private JwtUtils authService;

    // Fetch tenant-specific configuration
    @GetMapping
    public ResponseEntity<TenantConfiguration> getTenantConfiguration(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // Remove "Bearer "
        Long shopId = authService.extractShopId(token); // Extract shopId from token

        TenantConfiguration configuration = tenantConfigurationService.getConfigurationByShopId(shopId);
        if (configuration != null) {
            return ResponseEntity.ok(configuration);
        }
        return ResponseEntity.notFound().build(); // Handle not found case
    }

    // Update tenant-specific configuration
    @PutMapping
    public ResponseEntity<TenantConfiguration> updateTenantConfiguration(@RequestHeader("Authorization") String authHeader,
                                                                         @RequestBody TenantConfiguration config) {
        String token = authHeader.substring(7); // Remove "Bearer "
        Long shopId = authService.extractShopId(token); // Extract shopId from token

        TenantConfiguration updatedConfig = tenantConfigurationService.updateConfiguration(shopId, config);
        if (updatedConfig != null) {
            return ResponseEntity.ok(updatedConfig);
        }
        return ResponseEntity.notFound().build(); // Handle not found case
    }

    // Create a new tenant-specific configuration
    @PostMapping
    public ResponseEntity<TenantConfiguration> createTenantConfiguration(@RequestBody TenantConfiguration config) {
        TenantConfiguration createdConfig = tenantConfigurationService.createConfiguration(config);
        return ResponseEntity.status(201).body(createdConfig); // Return 201 Created
    }
}
