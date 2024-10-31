package com.laptop.Laptop.repository;

import com.laptop.Laptop.entity.Product;
import com.laptop.Laptop.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByShopId(Long shopId);
    List<Product> findByShop(Shop shop);
    Page<Product> findByShop(Shop shop, Pageable pageable);
    long countByShop(Shop shop);
    Page<Product> findAllByStockLessThanEqual(int stock, Pageable pageable);

   // Page<Product> findTopSellingProducts(Pageable pageable);
   @Query("SELECT p FROM Product p WHERE p.shop.id = :shopId AND p.quantitySold > 0 ORDER BY p.quantitySold DESC")
   Page<Product> findTopSellingProductsByShop(@Param("shopId") Long shopId, Pageable pageable);

    Page<Product> findAllByShopAndStockLessThanEqual(Shop shop, int lowStockThreshold, Pageable pageable);
}
