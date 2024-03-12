package com.springbatch.controller;

import com.springbatch.model.Product;
import com.springbatch.repository.ProductRepository;
import com.springbatch.repository.ProductRepositoryTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/data")
@RequiredArgsConstructor
public class ProductController {
    private final JobLauncher jobLauncher;
    private final Job job;
    private final ProductRepository productRepository;
    private final ProductRepositoryTemplate productRepositoryTemplate;

    @PostMapping
    public void importCsvToDb(){
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis()+100000)
                .toJobParameters();
        try {
            jobLauncher.run(job, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobParametersInvalidException |
                 JobInstanceAlreadyCompleteException | JobRestartException e) {
            e.printStackTrace();
        }
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProduct(){
        return ResponseEntity.ok(productRepository.get());
    }

    @PostMapping("/executequery")
    @ResponseStatus(HttpStatus.OK)
    public void truncateProductTable(@RequestParam("file") String fileName){
        productRepositoryTemplate.truncateProductTable(fileName);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<Product>> getById(@PathVariable int id){
        return ResponseEntity.ok(productRepository.getById(id));
    }
}
