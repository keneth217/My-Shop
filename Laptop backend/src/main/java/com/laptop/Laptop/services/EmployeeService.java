package com.laptop.Laptop.services;

import com.laptop.Laptop.dto.EmployeeDto; // Import EmployeeDto
import com.laptop.Laptop.dto.PaymentResponseDto;
import com.laptop.Laptop.dto.SalaryDto;
import com.laptop.Laptop.entity.*;
import com.laptop.Laptop.enums.ExpenseType;
import com.laptop.Laptop.enums.Roles;
import com.laptop.Laptop.repository.EmployeeRepository;
import com.laptop.Laptop.repository.ExpenseRepository;
import com.laptop.Laptop.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    // Utility method to get the logged-in user's details from the authentication token
    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal(); // Ensure User implements UserDetails
    }

    private Shop getUserShop() {
        return getLoggedInUser().getShop();
    }

    @Transactional
    public EmployeeDto addEmployee(EmployeeDto employeeDto) {
        User loggedInUser = getLoggedInUser();
        Employee employee = Employee.builder()
                .name(employeeDto.getName())
                .salary(employeeDto.getSalary())
                .phoneNumber(employeeDto.getPhoneNumber())
                .shop(loggedInUser.getShop())
                .role(Roles.CASHIER)
                .user(loggedInUser) // Set the user if needed
                .build();
        Employee savedEmployee = employeeRepository.save(employee);

        // Return the DTO of the saved employee
        return mapToDto(savedEmployee);
    }

    @Transactional
    public PaymentResponseDto payEmployee(Long employeeId, SalaryDto salary) {
        User loggedInUser = getLoggedInUser();

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
                .type(ExpenseType.SALARY)
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

    public List<EmployeeDto> getEmployeesForShop() {
        Shop shop = getUserShop();  // Get the logged-in user's shop
        User loggedInUser = getLoggedInUser(); // Retrieve the logged-in user
        String employerRole = String.valueOf(loggedInUser.getRole()); // Get employer's role

        List<Employee> employees = employeeRepository.findByShop(shop);

        // Convert each Employee to EmployeeDto and include employer's role
        return employees.stream()
                .map(employee -> mapToDto(employee, employerRole)) // Pass employer role
                .collect(Collectors.toList());
    }

    // Helper method to convert Employee to EmployeeDto with employerRole
    private EmployeeDto mapToDto(Employee employee, String employerRole) {
        return EmployeeDto.builder()
                .id(employee.getId())
                .name(employee.getName())
                .salary(employee.getSalary())
                .role(employee.getRole())
                .phoneNumber(employee.getPhoneNumber())
                .shopId(employee.getShop().getId())
                .employerRole(employerRole) // Set employer's role
                .build();
    }
}
