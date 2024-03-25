package com.springbatch.config;

import com.springbatch.model.Employee;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

public class CustomItemProcessor implements ItemProcessor<Employee, Employee> {
    @Override
    public Employee process(Employee employee) {
        return employee;
    }
}
