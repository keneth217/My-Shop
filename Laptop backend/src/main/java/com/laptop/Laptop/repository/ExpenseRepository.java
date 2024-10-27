package com.laptop.Laptop.repository;

import com.laptop.Laptop.entity.Expense;
import com.laptop.Laptop.entity.Shop;
import com.laptop.Laptop.enums.ExpenseType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByType(ExpenseType type); // Use enum directly

    List<Expense> findByShop(Shop shop);

    List<Expense> findByShopIdAndShopCode(Long shopId, String shopCode);

    List<Expense> findByShopAndType(Shop shop, ExpenseType type); // Use enum directly

    Page<Expense> findAllByType(Shop shop ,ExpenseType type, Pageable pageable); // Use enum directly

    Page<Expense> findAllByShopAndType(Shop shop, ExpenseType type, Pageable pageable); // Use enum directly

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.type = :type AND e.shop = :shop")
    Double sumAmountByTypeAndShop(@Param("type") ExpenseType type, @Param("shop") Shop shop);
}