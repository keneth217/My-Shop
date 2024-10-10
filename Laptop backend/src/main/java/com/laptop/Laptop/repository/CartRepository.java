package com.laptop.Laptop.repository;

import com.laptop.Laptop.entity.Cart;
import com.laptop.Laptop.entity.User;

import java.util.Optional;

public interface CartRepository {
    Optional<Cart> findByUser(User user);
}
