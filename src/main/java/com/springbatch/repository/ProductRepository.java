package com.springbatch.repository;

import com.springbatch.model.Product;
import org.hibernate.annotations.processing.SQL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {
    @Query(value = "select * from product", nativeQuery = true)
    public List<Product> get();

    @Query(value = "select * from product where product_id=?1", nativeQuery = true)
    public List<Product> getById(int id);

}
