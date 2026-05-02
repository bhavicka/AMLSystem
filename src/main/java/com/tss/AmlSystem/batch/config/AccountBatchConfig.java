package com.tss.AmlSystem.batch.config;

import com.tss.AmlSystem.batch.listener.FileJobExecutionListener;
import com.tss.AmlSystem.batch.processor.AccountItemProcessor;
import com.tss.AmlSystem.batch.writer.AccountItemWriter;
import com.tss.AmlSystem.dto.request.AccountBatchProcessDto;
import com.tss.AmlSystem.entity.tenant.Account;
import org.springframework.batch.core.configuration.annotation.EnableJdbcJobRepository;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJdbcJobRepository
public class AccountBatchConfig {

    @Bean
    public Step accountProcessingStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<AccountBatchProcessDto> reader,
            AccountItemProcessor processor,
            AccountItemWriter writer
    ) {
        return new StepBuilder("accountProcessingStep", jobRepository)
                .<AccountBatchProcessDto, Account>chunk(1000, transactionManager)
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
    public Job accountImportJob(
            JobRepository jobRepository,
            Step accountProcessingStep,
            FileJobExecutionListener listener
    ) {
        return new JobBuilder("accountImportJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(accountProcessingStep)
                .listener(listener)
                .build();

    }
}