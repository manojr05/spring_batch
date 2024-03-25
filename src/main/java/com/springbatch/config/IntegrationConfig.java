package com.springbatch.config;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.file.FileNameGenerator;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.messaging.Message;

import java.io.File;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableIntegration
public class IntegrationConfig {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job fileProcessingJob;

    private Set<String> processedFiles = new HashSet<>();


    @Bean
    @InboundChannelAdapter(value = "fileInputChannel", poller = @Poller(fixedDelay = "2000"))
    public FileReadingMessageSource fileReadingMessageSource() {
        CompositeFileListFilter<File> filter = new CompositeFileListFilter<>();
        filter.addFilters(new SimplePatternFileListFilter("*.csv"));
        FileReadingMessageSource reader = new FileReadingMessageSource();
        reader.setDirectory(new File("/home/manoj/Project/Source"));
        reader.setFilter(filter);
        return reader;
    }

    @Bean
    @ServiceActivator(inputChannel= "fileInputChannel")
    public FileWritingMessageHandler fileWritingMessageHandler() {
        FileWritingMessageHandler writer=new FileWritingMessageHandler(new File("/home/manoj/Project/Destination"));
        writer.setAutoCreateDirectory(true);
        writer.setExpectReply(false);
        writer.setFileExistsMode(FileExistsMode.REPLACE);
        writer.setFileNameGenerator(s->{
            return "input.csv";
        });
        return writer;
    }

    @ServiceActivator(inputChannel = "fileInputChannel")
    public void handleFileWrite(File file) throws Exception {
        if (!processedFiles.contains(file.getName())) {
            processedFiles.add(file.getName());
            launchBatchJob();
        }
    }

    private void launchBatchJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(fileProcessingJob, jobParameters);
    }

}

