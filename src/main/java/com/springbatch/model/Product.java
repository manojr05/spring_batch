package com.springbatch.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Product {
    @Id
    private int productId;
    private String title;
    private String description;
    private double price;
    private String discount;
}
