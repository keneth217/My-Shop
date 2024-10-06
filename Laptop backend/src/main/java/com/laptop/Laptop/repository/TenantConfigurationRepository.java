package com.laptop.Laptop.repository;

import com.laptop.Laptop.entity.TenantConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantConfigurationRepository extends JpaRepository<TenantConfiguration, Long> {

    TenantConfiguration findByShopId(Long shopId); // Find config by shopId
}