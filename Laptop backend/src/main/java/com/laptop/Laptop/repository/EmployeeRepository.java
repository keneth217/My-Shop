package com.laptop.Laptop.repository;

import com.laptop.Laptop.entity.Employee;
import com.laptop.Laptop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    long countByShop(Shop shop);

    List<Employee> findByShop(Shop shop);
}
