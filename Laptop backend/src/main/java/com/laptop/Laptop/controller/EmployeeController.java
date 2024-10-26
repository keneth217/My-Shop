package com.laptop.Laptop.controller;

import com.laptop.Laptop.constants.MyConstants;
import com.laptop.Laptop.dto.PaymentResponseDto;
import com.laptop.Laptop.dto.Responses.Responsedto;
import com.laptop.Laptop.dto.SalaryDto;
import com.laptop.Laptop.entity.Employee;
import com.laptop.Laptop.entity.Supplier;
import com.laptop.Laptop.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    private  EmployeeService employeeService;
    // Endpoint for paying employee salary
    @PostMapping("/{employeeId}/pay")
    public ResponseEntity<PaymentResponseDto> payEmployee(
            @PathVariable Long employeeId,
            @RequestBody SalaryDto salary) {

        // Call the service to handle the salary payment
        PaymentResponseDto response = employeeService.payEmployee(employeeId, salary);

        // Return the response with HTTP 200 OK status
        return ResponseEntity.ok(response);
    }

    // Add a new employee and automatically track their salary as an expense
    @PostMapping
    public ResponseEntity<Responsedto> addEmployee(@RequestBody Employee employee) {
        Employee addedEmployee = employeeService.addEmployee(employee);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body( new Responsedto(MyConstants.EMPLOYEE_CREATION,MyConstants.EMPLOYEE_CREATION_CODE));// Use 201 Created
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getSuppliers(){
        List<Employee> employees=employeeService.getExpenseForShop();
        if (employees.isEmpty()){
            return  ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(employees);
    }
}
