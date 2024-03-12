package com.springbatch.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Repository
public class ProductRepositoryTemplate {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ProductRepositoryTemplate(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Transactional
    public void truncateProductTable(String fileName) {
        try {
            // Load SQL file from classpath
            ClassPathResource resource = new ClassPathResource(fileName+".sql");
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            StringBuilder queryBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                queryBuilder.append(line);
            }
            // Execute the query
            System.out.println("Hibernate: "+queryBuilder.toString());
            jdbcTemplate.execute(queryBuilder.toString());
        } catch (IOException e) {
            // Handle file loading exception
            e.printStackTrace();
        }
    }
}

