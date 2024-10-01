package com.laptop.Laptop.repository;

import com.laptop.Laptop.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public  interface PaymentRepository extends JpaRepository<Payment,Long> {
}
