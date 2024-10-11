package com.laptop.Laptop.repository;

import com.laptop.Laptop.entity.Cart;
import com.laptop.Laptop.entity.Shop;
import com.laptop.Laptop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,Long> {
    Optional<Cart> findByUser(User user);

    Optional<Cart> findByUserAndShop(User user, Shop shop);
}
