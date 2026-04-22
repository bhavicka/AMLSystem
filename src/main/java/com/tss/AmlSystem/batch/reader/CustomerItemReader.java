package com.tss.AmlSystem.batch.reader;

import com.tss.AmlSystem.dto.request.CustomerDTO;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.infrastructure.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.infrastructure.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class CustomerItemReader {


    public FlatFileItemReader<CustomerDTO> customerReader(String filePath) {
        FlatFileItemReader<CustomerDTO> reader = new FlatFileItemReader<>();
        reader.setName("customerCsvReader");
        if (filePath != null)
            reader.setResource(new FileSystemResource(filePath));
        reader.setLinesToSkip(1);

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("clientNumber","firstName","lastName","middleName",
                "aadharNumber","pan","occupation","occupationType",
                "isPep","riskRate","monthlyIncome","dob",
                "professionMultiplier","familyCode");

        BeanWrapperFieldSetMapper<CustomerDTO> mapper = new BeanWrapperFieldSetMapper<>();
        mapper.setTargetType(CustomerDTO.class);

        DefaultLineMapper<CustomerDTO> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(mapper);
        reader.setLineMapper(lineMapper);
        return reader;
    }
}
