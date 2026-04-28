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

import org.springframework.batch.infrastructure.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.infrastructure.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.infrastructure.item.file.transform.DelimitedLineTokenizer;
import com.tss.AmlSystem.entity.enums.LogTag;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class TransactionItemReader {

    @Bean
    @StepScope
    public FlatFileItemReader<TransactionBatchProcessDto> transactionReader(
            @Value("#{jobParameters['filePath']}") String filePath
    ) {
        Assert.hasText(filePath, "Job parameter 'filePath' is required");
        log.debug("{} Attempting to configure CSV Reader for file: {}", LogTag.BATCH.getValue(), filePath);

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(FileHeaders.TRANSACTION_HEADER.toArray(new String[0]));

        BeanWrapperFieldSetMapper<TransactionBatchProcessDto> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(TransactionBatchProcessDto.class);

        DefaultLineMapper<TransactionBatchProcessDto> lineMapper = new DefaultLineMapper<>() {
            @Override
            public TransactionBatchProcessDto mapLine(String line, int lineNumber) throws Exception {
                TransactionBatchProcessDto dto = super.mapLine(line, lineNumber);
                dto.setRowNumber(lineNumber);
                return dto;
            }
        };
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return new FlatFileItemReaderBuilder<TransactionBatchProcessDto>()
                .name("transactionCsvReader")
                .resource(new FileSystemResource(filePath))
                .strict(true)
                .linesToSkip(1)
                .lineMapper(lineMapper)
                .build();
    }
}