package com.springbatch.config;

import com.springbatch.model.Employee;
import com.springbatch.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.io.PathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class SpringBatchConfig {

    private final EmployeeRepository productRepository;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job createJobBean(){
        return new JobBuilder("import_csv", jobRepository)
                .start(steps())
                .build();
    }

    @Bean
    public Step steps(){
        return new StepBuilder("step", jobRepository)
                .<Employee, Employee>chunk(20, transactionManager)
                .reader(reader(null))
                .processor(itemProcesser())
                .writer(itemWriter())
                .build();
    }

    @Bean
    @Scope(value = "step", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public FlatFileItemReader<Employee> reader(@Value("#{jobParameters['filePath']}") String filePath){
        return new FlatFileItemReaderBuilder<Employee>()
                .name("item_reader")
                .resource(new PathResource(filePath))
                .linesToSkip(1)
                .delimited()
                .names("id", "name", "age", "gender", "salary")
                .targetType(Employee.class)
                .build();
    }

    @Bean
    public ItemProcessor<Employee, Employee> itemProcesser(){
        return new CustomItemProcessor();
    }

    @Bean
    public RepositoryItemWriter<Employee> itemWriter(){
        RepositoryItemWriter<Employee> writer = new RepositoryItemWriter<>();
        writer.setRepository(productRepository);
        writer.setMethodName("save");
        return writer;
    }
}
