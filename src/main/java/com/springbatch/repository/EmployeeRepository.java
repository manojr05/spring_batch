package com.springbatch.repository;

import com.springbatch.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, String> {
    @Query(value = "SELECT * FROM employee ORDER BY age DESC LIMIT 5;", nativeQuery = true)
    List<Employee> topFiveBasedOnAge();

    @Query(value = "SELECT * FROM employee ORDER BY age LIMIT 5;", nativeQuery = true)
    List<Employee> bottomFiveBasedOnAge();

    List<Employee> findTop5ByOrderByAgeDesc();
    List<Employee> findTop5ByOrderByAge();
}
