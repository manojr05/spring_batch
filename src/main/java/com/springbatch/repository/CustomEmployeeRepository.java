package com.springbatch.repository;

import com.springbatch.model.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class CustomEmployeeRepository {

    @PersistenceContext
    EntityManager entityManager;

    public List<Employee> topFiveBasedOnAge(){
        return entityManager.createQuery("select e from Employee e order by e.age desc", Employee.class)
                .setMaxResults(5).getResultList();
    }

    public List<Employee> bottomFiveBasedOnAge(){
        return entityManager.createQuery("select e from Employee e order by e.age", Employee.class)
                .setMaxResults(5).getResultList();
    }
}
