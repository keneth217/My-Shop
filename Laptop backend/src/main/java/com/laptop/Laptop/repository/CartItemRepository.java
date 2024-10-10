package com.laptop.Laptop.repository;

import com.laptop.Laptop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<Cart,Long> {
}
