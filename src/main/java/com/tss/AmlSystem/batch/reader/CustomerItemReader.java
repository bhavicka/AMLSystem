package com.tss.AmlSystem.batch.reader;

import com.tss.AmlSystem.dto.request.CustomerDTO;
import com.tss.AmlSystem.service.FileValidationService;
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
public class CustomerItemReader {

    private final FileValidationService fileValidationService;

    @Bean
    @StepScope
    public FlatFileItemReader<CustomerDTO> customerReader(
            @Value("#{jobParameters['filePath']}") String filePath,
            @Value("#{jobParameters['fileId']}") Long fileId
    ) {
        Assert.hasText(filePath, "Job parameter 'filePath' is required");
        return new FlatFileItemReaderBuilder<CustomerDTO>()
                .name("customerCsvReader")
                .resource(new FileSystemResource(filePath))
                .strict(true)
                .linesToSkip(1)
                .skippedLinesCallback(line -> fileValidationService.validateCustomerHeader(line, fileId))
                .delimited()
                .delimiter(",")
                .strict(true)
                .names(fileValidationService.getCustomerHeaders().toArray(String[]::new))
                .targetType(CustomerDTO.class)
                .build();
    }
}
