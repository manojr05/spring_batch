package com.springbatch.config;

import com.springbatch.model.Product;
import com.springbatch.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class SpringBatchConfig {

    private final ProductRepository productRepository;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    @Value("${file.input}")
    private String fileInput;

    @Bean
    public Job createJobBean(){
        return new JobBuilder("import_csv", jobRepository)
                .start(steps())
                .build();
    }

    @Bean
    public Step steps(){
        return new StepBuilder("step", jobRepository)
                .<Product, Product>chunk(10, transactionManager)
                .reader(reader())
                .processor(itemProcesser())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public FlatFileItemReader<Product> reader(){
        return new FlatFileItemReaderBuilder<Product>()
                .name("item_reader")
                .resource(new FileSystemResource(fileInput))
                .linesToSkip(1)
                .delimited()
                .names("productId", "title", "description", "price", "discount")
                .targetType(Product.class)
                .build();
    }

    @Bean
    public ItemProcessor<Product, Product> itemProcesser(){
        return new CustomItemProcessor();
    }

    @Bean
    public RepositoryItemWriter<Product> itemWriter(){
        RepositoryItemWriter<Product> writer = new RepositoryItemWriter<>();
        writer.setRepository(productRepository);
        writer.setMethodName("save");
        return writer;
    }

}
