package com.tss.AmlSystem.batch.processor;

import com.tss.AmlSystem.dto.request.AccountBatchProcessDto;
import com.tss.AmlSystem.entity.enums.tenant.FileType;
import com.tss.AmlSystem.entity.tenant.Account;
import com.tss.AmlSystem.entity.tenant.File;
import com.tss.AmlSystem.entity.tenant.FileValidationErrors;
import com.tss.AmlSystem.factory.FileValidatorFactory;
import com.tss.AmlSystem.mapper.AccountMapper;
import com.tss.AmlSystem.repository.CustomerRepository;
import com.tss.AmlSystem.strategy.batch.file.FileValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.file.FlatFileParseException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.SmartValidator;

import java.util.List;
import com.tss.AmlSystem.entity.enums.LogTag;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@StepScope
@Slf4j
public class AccountItemProcessor implements ItemProcessor<AccountBatchProcessDto, Account>, StepExecutionListener, SkipListener<AccountBatchProcessDto, Account> {

    private final AccountMapper accountMapper;

    private FileValidator fileValidator;
    private final SmartValidator smartValidator;
    private final FileValidatorFactory factory;

    private final CustomerRepository customerRepository;

    private File fileEntity;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        fileValidator = factory.getValidator(FileType.ACCOUNTS);
        Long fileId = stepExecution.getJobParameters().getLong("fileId");
        this.fileEntity = fileValidator.getFile(fileId);
        log.info("{} Initialized AccountItemProcessor for File ID: {}", LogTag.BATCH.getValue(), fileId);
    }

    @Override
    public Account process(AccountBatchProcessDto dto) {
        int rowNumber = dto.getRowNumber();

        DataBinder binder = new DataBinder(dto);
        binder.setValidator(smartValidator);
        binder.validate();
        BindingResult results = binder.getBindingResult();

        if (results.hasErrors()) {
            handleValidationErrors(results, fileEntity, rowNumber);
            return null;
        }
        if (!customerRepository.existsByClientNumber(dto.getClientNumber())) {
            FileValidationErrors error = new FileValidationErrors();
            error.setFile(fileEntity);
            error.setRowNumber(rowNumber);
            error.setFieldName("clientNumber");
            error.setErrorMessage("Customer with client number " + dto.getClientNumber() + " does not exist.");
            fileValidator.saveValidationErrors(List.of(error));
            log.warn("{} Skipping account for clientNumber {} at row {} because customer does not exist", LogTag.BATCH.getValue(), dto.getClientNumber(), rowNumber);
            return null; // Skip this row
        }
        try {
            Account account = accountMapper.toAccount(dto);
            account.setFile(fileEntity);
            return account;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void onSkipInRead(Throwable t) {
        log.warn("{} Skipping item in read phase: {}", LogTag.BATCH.getValue(), t.getMessage());
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
    public void onSkipInWrite(Account item, Throwable t) {
        log.warn("{} Skipping account write due to error: {}", LogTag.BATCH.getValue(), t.getMessage());
        FileValidationErrors error = new FileValidationErrors();
        error.setFile(fileEntity);
        error.setRowNumber(0);
        error.setFieldName("Database");
        error.setErrorMessage("Persistence error: " + t.getMessage());
        fileValidator.saveValidationErrors(List.of(error));
    }

    @Override
    public void onSkipInProcess(AccountBatchProcessDto item, Throwable t) {
        log.warn("{} Skipping item processing at row: {} due to error: {}", LogTag.BATCH.getValue(), item.getRowNumber(), t.getMessage());
        FileValidationErrors error = new FileValidationErrors();
        error.setFile(fileEntity);
        error.setRowNumber(item.getRowNumber());
        error.setFieldName("Processing");
        error.setErrorMessage("Processing error: " + t.getMessage());
        fileValidator.saveValidationErrors(List.of(error));
    }

    private void handleValidationErrors(BindingResult results, File fileEntity, int rowNumber) {
        log.debug("{} Validation failed for row {} with {} errors", LogTag.BATCH.getValue(), rowNumber, results.getErrorCount());
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

