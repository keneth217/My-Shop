package com.laptop.Laptop.repository;

import com.laptop.Laptop.entity.Product;
import com.laptop.Laptop.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByShopId(Long shopId);
    List<Product> findByShop(Shop shop);
    Page<Product> findByShop(Shop shop, Pageable pageable);
    long countByShop(Shop shop);
    Page<Product> findAllByStockLessThanEqual(int stock, Pageable pageable);

   // Page<Product> findTopSellingProducts(Pageable pageable);
   @Query("SELECT p FROM Product p ORDER BY p.quantitySold DESC") // Assuming salesCount tracks the number of sales
   Page<Product> findTopSellingProducts(Pageable pageable);

}
