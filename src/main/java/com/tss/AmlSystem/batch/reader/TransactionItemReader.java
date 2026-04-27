package com.tss.AmlSystem.batch.reader;

import com.tss.AmlSystem.dto.request.TransactionBatchProcessDto;
import com.tss.AmlSystem.utils.FileHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.Assert;

@Configuration
@RequiredArgsConstructor
public class TransactionItemReader {

    @Bean
    @StepScope
    public FlatFileItemReader<TransactionBatchProcessDto> transactionReader(
            @Value("#{jobParameters['filePath']}") String filePath
    ) {
        Assert.hasText(filePath, "Job parameter 'filePath' is required");
        return new FlatFileItemReaderBuilder<TransactionBatchProcessDto>()
                .name("transactionCsvReader")
                .resource(new FileSystemResource(filePath))
                .strict(true)
                .linesToSkip(1)
                .delimited()
                .delimiter(",")
                .names(FileHeaders.TRANSACTION_HEADER.toArray(new String[0]))
                .targetType(TransactionBatchProcessDto.class)
                .build();
    }
}