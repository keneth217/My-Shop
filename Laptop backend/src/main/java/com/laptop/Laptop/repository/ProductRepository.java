package com.laptop.Laptop.repository;

import com.laptop.Laptop.entity.Product;
import com.laptop.Laptop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByShopId(Long shopId);
    List<Product> findByShop(Shop shop);
}
