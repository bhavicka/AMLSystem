package com.tss.AmlSystem.batch.reader;

import com.tss.AmlSystem.dto.request.AccountBatchProcessDto;
import com.tss.AmlSystem.dto.request.CustomerBatchProcessDto;
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
public class AccountItemReader {

    @Bean
    @StepScope
    public FlatFileItemReader<AccountBatchProcessDto> accountReader(
            @Value("#{jobParameters['filePath']}") String filePath
    ) {
        Assert.hasText(filePath, "Job parameter 'filePath' is required");
        log.debug("{} Attempting to configure CSV Reader for file: {}", LogTag.BATCH.getValue(), filePath);

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(FileHeaders.ACCOUNT_HEADER.toArray(new String[0]));

        BeanWrapperFieldSetMapper<AccountBatchProcessDto> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(AccountBatchProcessDto.class);

        DefaultLineMapper<AccountBatchProcessDto> lineMapper = new DefaultLineMapper<>() {
            @Override
            public AccountBatchProcessDto mapLine(String line, int lineNumber) throws Exception {
                AccountBatchProcessDto dto = super.mapLine(line, lineNumber);
                dto.setRowNumber(lineNumber);
                return dto;
            }
        };
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return new FlatFileItemReaderBuilder<AccountBatchProcessDto>()
                .name("accountCsvReader")
                .resource(new FileSystemResource(filePath))
                .strict(true)
                .linesToSkip(1)
                .lineMapper(lineMapper)
                .build();
    }
}