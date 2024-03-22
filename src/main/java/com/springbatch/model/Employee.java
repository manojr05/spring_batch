package com.springbatch.model;

import com.springbatch.model.enums.Gender;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    private int id;
    private String name;
    private int age;
    private Gender gender;
    private double salary;
}
