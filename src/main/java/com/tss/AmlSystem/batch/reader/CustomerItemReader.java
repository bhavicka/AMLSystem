package com.tss.AmlSystem.batch.reader;

import com.tss.AmlSystem.dto.request.CustomerDTO;
import com.tss.AmlSystem.service.FileValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.infrastructure.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.infrastructure.item.file.transform.DelimitedLineTokenizer;
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
    ) throws Exception {
        Assert.hasText(filePath, "Job parameter 'filePath' is required");

        FlatFileItemReader<CustomerDTO> reader = new FlatFileItemReader<>();
        reader.setName("customerCsvReader");
        reader.setResource(new FileSystemResource(filePath));
        reader.setStrict(true);
        reader.setLinesToSkip(1);
        reader.setSkippedLinesCallback(line -> fileValidationService.validateCustomerHeader(line, fileId));

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(DelimitedLineTokenizer.DELIMITER_COMMA);
        tokenizer.setStrict(true);
        tokenizer.setNames(fileValidationService.getCustomerHeaders().toArray(String[]::new));

        BeanWrapperFieldSetMapper<CustomerDTO> mapper = new BeanWrapperFieldSetMapper<>();
        mapper.setTargetType(CustomerDTO.class);

        DefaultLineMapper<CustomerDTO> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(mapper);

        reader.setLineMapper(lineMapper);
        reader.afterPropertiesSet();
        return reader;
    }
}
