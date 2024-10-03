package com.laptop.Laptop.repository;

import com.laptop.Laptop.entity.Investment;
import com.laptop.Laptop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface InvestmentRepository extends JpaRepository<Investment, Long> {
    List<Investment> findByShop(Shop shop);

    Optional<Investment> findByShopIdAndShopCode(Long shopId, String shopCode);
}
