package com.laptop.Laptop.repository;

import com.laptop.Laptop.entity.Shop;
import com.laptop.Laptop.entity.StockPurchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface StockPurchaseRepository extends JpaRepository<StockPurchase, Long> {
    Page<StockPurchase> findByShop(Shop shop, Pageable pageable);

    List<StockPurchase> findByShopIdAndPurchaseDateBetween(Long shopId, LocalDate startDate, LocalDate endDate);

    List<StockPurchase> findByShopIdAndShopCode(Long shopId, String shopCode);

    List<StockPurchase> findByShop(Shop shop);

    List<StockPurchase> findByShopAndPurchaseDateBetween(Shop shop, LocalDate startDate, LocalDate endDate);
}
