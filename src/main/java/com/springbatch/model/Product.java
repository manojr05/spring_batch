package com.springbatch.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Product {
    @Id
    private String productId;
    private String title;
    private String description;
    private String price;
    private String discount;
}
