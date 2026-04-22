package com.tss.AmlSystem.batch.config;

import com.tss.AmlSystem.batch.listener.FileJobExecutionListener;
import com.tss.AmlSystem.batch.processor.CustomerItemProcessor;
import com.tss.AmlSystem.batch.reader.CustomerItemReader;
import com.tss.AmlSystem.batch.writer.CustomerItemWriter;
import com.tss.AmlSystem.dto.request.CustomerDTO;
import com.tss.AmlSystem.entity.tenant.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.EnableJdbcJobRepository;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.ChunkOrientedStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.infrastructure.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.infrastructure.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJdbcJobRepository
public class CustomerBatchConfig {

    @Bean
    public Step customerProcessingStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<CustomerDTO> reader,
            CustomerItemProcessor processor,
            CustomerItemWriter writer) {

        return new StepBuilder("customerProcessingStep", jobRepository)
                .<CustomerDTO, Customer>chunk(1000) // no TM here
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .transactionManager(transactionManager) // moved here
                .faultTolerant()
                .skipPolicy((t, skipCount) -> true)
                .build();
    }

    @Bean
    public Job customerImportJob(
            JobRepository jobRepository,
            Step customerProcessingStep,
            FileJobExecutionListener listener) {

        return new JobBuilder("customerImportJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(customerProcessingStep)
                .listener(listener)
                .build();
    }
}