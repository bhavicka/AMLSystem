package com.tss.AmlSystem.batch.processor;

import com.tss.AmlSystem.dto.request.CustomerBatchProcessDto;
import com.tss.AmlSystem.entity.enums.tenant.FileType;
import com.tss.AmlSystem.entity.tenant.Customer;
import com.tss.AmlSystem.entity.tenant.File;
import com.tss.AmlSystem.entity.tenant.FileValidationErrors;
import com.tss.AmlSystem.factory.FileValidatorFactory;
import com.tss.AmlSystem.mapper.CustomerMapper;
import com.tss.AmlSystem.strategy.batch.file.FileValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.StepExecutionListener;
import java.util.List;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.file.FlatFileParseException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.SmartValidator;

@Component
@RequiredArgsConstructor
@StepScope
public class CustomerItemProcessor implements ItemProcessor<CustomerBatchProcessDto, Customer>, StepExecutionListener, SkipListener<CustomerBatchProcessDto, Customer> {

    private final CustomerMapper customerMapper;
    private FileValidator fileValidator;
    private final SmartValidator smartValidator;
    private final FileValidatorFactory factory;

    private File fileEntity;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        fileValidator = factory.getValidator(FileType.CUSTOMERS);
        Long fileId = stepExecution.getJobParameters().getLong("fileId");
        this.fileEntity = fileValidator.getFile(fileId);
    }

    @Override
    public Customer process(CustomerBatchProcessDto dto) {
        int rowNumber = dto.getRowNumber();

        DataBinder binder = new DataBinder(dto);
        binder.setValidator(smartValidator);
        binder.validate();
        BindingResult results = binder.getBindingResult();

        if (results.hasErrors()) {
            handleValidationErrors(results, fileEntity, rowNumber);
            return null;
        }
        Customer customer = customerMapper.toCustomer(dto);
        customer.setFile(fileEntity);
        return customer;
    }

    @Override
    public void onSkipInRead(Throwable t) {
        if (t instanceof FlatFileParseException ffpe) {
            FileValidationErrors error = new FileValidationErrors();
            error.setFile(fileEntity);
            error.setRowNumber(ffpe.getLineNumber());
            error.setFieldName("Row");
            error.setErrorMessage("Parsing error: " + ffpe.getMessage());
            fileValidator.saveValidationErrors(List.of(error));
        }
    }

    @Override
    public void onSkipInWrite(Customer item, Throwable t) {
        FileValidationErrors error = new FileValidationErrors();
        error.setFile(fileEntity);
        error.setRowNumber(0); // Writer doesn't easily give row number
        error.setFieldName("Database");
        error.setErrorMessage("Persistence error: " + t.getMessage());
        fileValidator.saveValidationErrors(List.of(error));
    }

    @Override
    public void onSkipInProcess(CustomerBatchProcessDto item, Throwable t) {
        FileValidationErrors error = new FileValidationErrors();
        error.setFile(fileEntity);
        error.setRowNumber(item.getRowNumber());
        error.setFieldName("Processing");
        error.setErrorMessage("Processing error: " + t.getMessage());
        fileValidator.saveValidationErrors(List.of(error));
    }

    private void handleValidationErrors(BindingResult results, File fileEntity, int rowNumber) {
        List<FileValidationErrors> errorList = results.getFieldErrors().stream()
                .map(fieldError -> {
                    FileValidationErrors error = new FileValidationErrors();
                    error.setFile(fileEntity);
                    error.setRowNumber(rowNumber);
                    error.setFieldName(fieldError.getField());
                    error.setErrorMessage(fieldError.getDefaultMessage());
                    return error;
                })
                .toList();

        fileValidator.saveValidationErrors(errorList);
    }
}

