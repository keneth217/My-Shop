package com.laptop.Laptop.services;

import com.laptop.Laptop.entity.TenantConfiguration;
import com.laptop.Laptop.repository.TenantConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TenantConfigurationService {

    @Autowired
    private TenantConfigurationRepository tenantConfigurationRepository;

    // Fetch configuration by shopId
    public TenantConfiguration getConfigurationByShopId(Long shopId) {
        return tenantConfigurationRepository.findByShopId(shopId);
    }

    // Update configuration for a tenant
    public TenantConfiguration updateConfiguration(Long shopId, TenantConfiguration config) {
        TenantConfiguration existingConfig = tenantConfigurationRepository.findByShopId(shopId);
        if (existingConfig != null) {
            // Update fields as necessary
            existingConfig.setTheme(config.getTheme());
            existingConfig.setLogoUrl(config.getLogoUrl());
            existingConfig.setFeatureXEnabled(config.isFeatureXEnabled());
            return tenantConfigurationRepository.save(existingConfig);
        }
        return null; // Or handle this case as needed
    }

    // Create new configuration
    public TenantConfiguration createConfiguration(TenantConfiguration config) {
        return tenantConfigurationRepository.save(config);
    }
}
