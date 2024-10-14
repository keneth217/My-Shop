package com.laptop.Laptop.repository;

import com.laptop.Laptop.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReceiptRepository  extends JpaRepository<Receipt,Long> {
    // Custom query to find the maximum receipt number
    @Query("SELECT MAX(r.receiptNo) FROM Receipt r")
    Optional<Integer> findMaxReceiptNo();
}
