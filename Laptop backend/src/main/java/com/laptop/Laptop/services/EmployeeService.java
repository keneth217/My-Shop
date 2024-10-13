package com.laptop.Laptop.services;

import com.laptop.Laptop.dto.PaymentResponseDto;
import com.laptop.Laptop.dto.SalaryDto;
import com.laptop.Laptop.entity.Employee;
import com.laptop.Laptop.entity.Expense;
import com.laptop.Laptop.entity.Payment;
import com.laptop.Laptop.entity.User;
import com.laptop.Laptop.enums.ExpenseType;
import com.laptop.Laptop.helper.AuthUser;
import com.laptop.Laptop.repository.EmployeeRepository;
import com.laptop.Laptop.repository.ExpenseRepository;
import com.laptop.Laptop.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AuthUser authUser;

    @Transactional
    public Employee addEmployee(Employee employee) {
        User loggedInUser = authUser.getLoggedInUserDetails();
        employee.setShop(loggedInUser.getShop());
        return employeeRepository.save(employee);
    }
    @Transactional
    public PaymentResponseDto payEmployee(Long employeeId, SalaryDto salary) {
        User loggedInUser = authUser.getLoggedInUserDetails();

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalStateException("Employee not found"));

        if (!employee.getShop().getId().equals(loggedInUser.getShop().getId())) {
            throw new IllegalStateException("Unauthorized to pay this employee");
        }

        Payment salaryPayment = Payment.builder()
                .employee(employee)
                .amount(salary.getSalaryAmount())
                .paymentDate(LocalDate.now())
                .build();
        paymentRepository.save(salaryPayment);

        Expense salaryExpense = Expense.builder()
                .expenseType(ExpenseType.SALARY.name())
                .amount(salary.getSalaryAmount())
                .date(LocalDate.now())
                .shopCode(loggedInUser.getShopCode())
                .user(loggedInUser)
                .shop(loggedInUser.getShop())
                .build();
        expenseRepository.save(salaryExpense);

        return new PaymentResponseDto("Salary payment successful",
                salary.getSalaryAmount(), employee.getName(), LocalDate.now());
    }

}
