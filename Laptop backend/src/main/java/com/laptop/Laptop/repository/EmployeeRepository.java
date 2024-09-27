package com.laptop.Laptop.repository;

import com.laptop.Laptop.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
