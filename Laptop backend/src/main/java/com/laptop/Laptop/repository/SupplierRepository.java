package com.laptop.Laptop.repository;

import com.laptop.Laptop.entity.Shop;
import com.laptop.Laptop.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier,Long> {
    List<Supplier> findByShop(Shop shop);
}
