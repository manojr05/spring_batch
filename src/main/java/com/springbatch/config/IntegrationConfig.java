package com.springbatch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
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
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.file.filters.FileSystemPersistentAcceptOnceFileListFilter;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.metadata.SimpleMetadataStore;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableIntegration
public class IntegrationConfig {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job fileProcessingJob;

    @Value("${watcher.directory}")
    String watcherDirectory;

    private Set<String> processedFiles = new HashSet<>();

    @Bean
    @InboundChannelAdapter(value = "fileInputChannel", poller = @Poller(fixedDelay = "5000"))
    public FileReadingMessageSource fileReadingMessageSource() {
        CompositeFileListFilter<File> filter = new CompositeFileListFilter<>();
        filter.addFilters(new SimplePatternFileListFilter("*.csv"));
        FileSystemPersistentAcceptOnceFileListFilter fileFilter = new FileSystemPersistentAcceptOnceFileListFilter(
                new SimpleMetadataStore(), "");
        filter.addFilters(fileFilter);
        FileReadingMessageSource reader = new FileReadingMessageSource();
        reader.setDirectory(new File(watcherDirectory));
        reader.setFilter(filter);
        return reader;
    }

    @ServiceActivator(inputChannel = "fileInputChannel")
    public void handleFileWrite(File file) throws Exception {
        String fileName = file.getName();
        if (!processedFiles.contains(fileName)) {
            processedFiles.add(fileName);
            launchBatchJob(fileName);
        }
    }

    private void launchBatchJob(String fileName) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis())
                .addString("filePath", watcherDirectory+fileName)
                .toJobParameters();

        jobLauncher.run(fileProcessingJob, jobParameters);
    }
}

