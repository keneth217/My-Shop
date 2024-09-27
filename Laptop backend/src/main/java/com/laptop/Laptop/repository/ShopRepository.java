package com.laptop.Laptop.repository;

import com.laptop.Laptop.entity.Shop;
import com.laptop.Laptop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository  extends JpaRepository<Shop,Long> {
}
