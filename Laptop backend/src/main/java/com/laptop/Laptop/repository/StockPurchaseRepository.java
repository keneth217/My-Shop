package com.laptop.Laptop.repository;

import com.laptop.Laptop.entity.StockPurchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockPurchaseRepository extends JpaRepository<StockPurchase, Long> {
}
