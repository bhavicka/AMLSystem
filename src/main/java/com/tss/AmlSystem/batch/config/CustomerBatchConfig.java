package com.tss.AmlSystem.batch.config;

import com.tss.AmlSystem.batch.listener.FileJobExecutionListener;
import com.tss.AmlSystem.batch.processor.CustomerItemProcessor;
import com.tss.AmlSystem.batch.writer.CustomerItemWriter;
import com.tss.AmlSystem.dto.request.CustomerBatchProcessDto;
import com.tss.AmlSystem.entity.tenant.Customer;
import org.springframework.batch.core.configuration.annotation.EnableJdbcJobRepository;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJdbcJobRepository
public class CustomerBatchConfig {
    @Bean
    public Step customerProcessingStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<CustomerBatchProcessDto> reader,
            CustomerItemProcessor processor,
            CustomerItemWriter writer
    ) {
        return new StepBuilder("customerProcessingStep", jobRepository)
                .<CustomerBatchProcessDto, Customer>chunk(1000, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(100)
                .retryLimit(3)
                .retry(org.springframework.dao.OptimisticLockingFailureException.class)
                .retry(org.springframework.dao.DeadlockLoserDataAccessException.class)
                .listener((StepExecutionListener) processor)
                .listener((SkipListener<?, ?>) processor)
                .build();
    }

    @Bean
    public Job customerImportJob(
            JobRepository jobRepository,
            Step customerProcessingStep,
            FileJobExecutionListener listener
    ) {
        return new JobBuilder("customerImportJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(customerProcessingStep)
                .listener(listener)
                .build();
    }
}
