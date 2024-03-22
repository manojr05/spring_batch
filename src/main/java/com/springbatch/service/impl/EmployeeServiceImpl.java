package com.springbatch.service.impl;

import com.springbatch.model.Employee;
import com.springbatch.repository.CustomEmployeeRepository;
import com.springbatch.repository.EmployeeRepository;
import com.springbatch.service.EmployeeService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final CustomEmployeeRepository customEmployeeRepository;

    @Override
    @Scheduled(fixedRateString = "${scheduler.fixedRateValue}")
    public void getRecords(){
        List<Employee> topFiveBasedOnAge = employeeRepository.findTop5ByOrderByAgeDesc();

        System.out.println(new Date().toString());

        System.out.println("Top 5 employees with highest age:");
        topFiveBasedOnAge.forEach(System.out::println);

        List<Employee> bottomFiveBasedOnAge = employeeRepository.findTop5ByOrderByAge();

        System.out.println("Bottom 5 employees with least age:");
        bottomFiveBasedOnAge.forEach(System.out::println);
    }
}
