package com.laptop.Laptop.repository;

import com.laptop.Laptop.entity.Cart;
import com.laptop.Laptop.entity.Sale;
import com.laptop.Laptop.entity.Shop;
import com.laptop.Laptop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<Sale> findByShopAndDateBetween(Shop shop, LocalDate startDate, LocalDate endDate);
    List<Sale> findByShopIdAndShopCode(Long shopId, String shopCode);
    Page<Sale> findAllByDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
    List<Sale> findByShop(Shop shop);

    List<Sale> findByShopIdAndDateBetween(Long shopId, LocalDate startDate, LocalDate endDate);

    Page<Sale> findAllByShopAndDateBetween(Shop shop, LocalDate startDate, LocalDate endDate, Pageable pageable);

    List<Sale> findByUserAndShop(User loggedInUser, Shop shop);


}
