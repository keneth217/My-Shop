package com.laptop.Laptop.services;

import com.laptop.Laptop.entity.Expense;
import com.laptop.Laptop.entity.User;
import com.laptop.Laptop.enums.ExpenseType;
import com.laptop.Laptop.helper.AuthUser;
import com.laptop.Laptop.repository.ExpenseRepository;
import com.laptop.Laptop.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
@Service
public class ExpenseServices {

    @Autowired
    private ExpenseRepository expenseRepository ;

    @Autowired
    private AuthUser authUser;
    User loggedInUser = authUser.getLoggedInUserDetails();

    public Expense addExpense(Expense expense) {

        expense.setShopCode(loggedInUser.getShopCode());
        expense.setUser(loggedInUser);
        expense.setDescription(expense.getDescription());
        expense.setExpenseType(ExpenseType.UTILITIES.name());
        expense.setAmount(expense.getAmount());
        expense.setDate(LocalDate.now());
        expense.setShop(loggedInUser.getShop());
        return expenseRepository.save(expense);
    }

    public List<Expense> findExpensesByType(ExpenseType expenseType) {
        return expenseRepository.findByShopAndExpenseType(loggedInUser.getShop(), expenseType.name());
    }


    public List<Expense> getExpensesByExpenseType(ExpenseType expenseType) {
        return null;
    }
}
