package com.laptop.Laptop.repository;

import com.laptop.Laptop.entity.Expense;
import com.laptop.Laptop.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByExpenseType(String expenseType);

    List<Expense> findByShop(Shop shop);
    List<Expense> findByShopIdAndShopCode(Long shopId, String shopCode);

    List<Expense> findByShopAndExpenseType(Shop shop, String name);

    Page<Expense> findAllByExpenseType(String name, Pageable pageable);
}
