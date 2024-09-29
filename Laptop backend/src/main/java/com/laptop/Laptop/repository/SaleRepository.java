package com.laptop.Laptop.repository;

import com.laptop.Laptop.entity.Sale;
import com.laptop.Laptop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<Sale> findByShopAndDateBetween(Shop shop, LocalDate startDate, LocalDate endDate);
    List<Sale> findByShopIdAndShopCode(Long shopId, String shopCode);

    List<Sale> findByShop(Shop shop);
}
