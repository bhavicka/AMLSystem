package com.tss.AmlSystem.batch.config;

import com.tss.AmlSystem.batch.listener.FileJobExecutionListener;
import com.tss.AmlSystem.batch.processor.AccountItemProcessor;
import com.tss.AmlSystem.batch.processor.TransactionItemProcessor;
import com.tss.AmlSystem.batch.writer.AccountItemWriter;
import com.tss.AmlSystem.batch.writer.TransactionItemWriter;
import com.tss.AmlSystem.dto.request.AccountBatchProcessDto;
import com.tss.AmlSystem.dto.request.TransactionBatchProcessDto;
import com.tss.AmlSystem.entity.tenant.Account;
import com.tss.AmlSystem.entity.tenant.Transaction;
import org.springframework.batch.core.configuration.annotation.EnableJdbcJobRepository;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJdbcJobRepository
public class TransactionBatchConfig {

    @Bean
    public Step transactionProcessingStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<TransactionBatchProcessDto> reader,
            TransactionItemProcessor processor,
            TransactionItemWriter writer
    ) {
        return new StepBuilder("transactionProcessingStep", jobRepository)
                .<TransactionBatchProcessDto, Transaction>chunk(1000, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(100)
                .retryLimit(3)
                .retry(org.springframework.dao.OptimisticLockingFailureException.class)
                .retry(org.springframework.dao.DeadlockLoserDataAccessException.class)
                .listener(processor)
                .build();
    }

    @Bean
    public Job transactionImportJob(
            JobRepository jobRepository,
            Step transactionProcessingStep,
            FileJobExecutionListener listener
    ) {
        return new JobBuilder("transactionImportJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(transactionProcessingStep)
                .listener(listener)
                .build();
    }
}
