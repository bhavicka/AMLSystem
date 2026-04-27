package com.tss.AmlSystem.batch.processor;

import com.tss.AmlSystem.dto.request.AccountBatchProcessDto;
import com.tss.AmlSystem.dto.request.TransactionBatchProcessDto;
import com.tss.AmlSystem.entity.enums.tenant.FileType;
import com.tss.AmlSystem.entity.tenant.Account;
import com.tss.AmlSystem.entity.tenant.File;
import com.tss.AmlSystem.entity.tenant.FileValidationErrors;
import com.tss.AmlSystem.entity.tenant.Transaction;
import com.tss.AmlSystem.factory.FileValidatorFactory;
import com.tss.AmlSystem.mapper.TransactionMapper;
import com.tss.AmlSystem.repository.AccountRepository;
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

@Component
@RequiredArgsConstructor
@StepScope
public class TransactionItemProcessor implements ItemProcessor<TransactionBatchProcessDto, Transaction>, StepExecutionListener, SkipListener<TransactionBatchProcessDto, Transaction> {

    private final TransactionMapper transactionMapper;

    private FileValidator fileValidator;
    private final SmartValidator smartValidator;
    private final FileValidatorFactory factory;

    private final AccountRepository accountRepository;

    private File fileEntity;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        fileValidator = factory.getValidator(FileType.TRANSACTIONS);
        Long fileId = stepExecution.getJobParameters().getLong("fileId");
        this.fileEntity = fileValidator.getFile(fileId);
    }

    @Override
    public Transaction process(TransactionBatchProcessDto dto) {
        int rowNumber = dto.getRowNumber();

        DataBinder binder = new DataBinder(dto);
        binder.setValidator(smartValidator);
        binder.validate();
        BindingResult results = binder.getBindingResult();

        if (results.hasErrors()) {
            handleValidationErrors(results, fileEntity, rowNumber);
            return null;
        }
        if (!accountRepository.existsByAccountNumber(dto.getAccountNumber().trim())) {
            FileValidationErrors error = new FileValidationErrors();
            error.setFile(fileEntity);
            error.setRowNumber(rowNumber);
            error.setFieldName("accountNumber");
            error.setErrorMessage("Account with number " + dto.getAccountNumber() + " does not exist.");
            fileValidator.saveValidationErrors(List.of(error));
            return null; // Skip this row
        }

        Transaction transaction = transactionMapper.toTransaction(dto);
        transaction.setFile(fileEntity);
        return transaction;
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
    public void onSkipInWrite(Transaction item, Throwable t) {
        FileValidationErrors error = new FileValidationErrors();
        error.setFile(fileEntity);
        error.setRowNumber(0);
        error.setFieldName("Database");
        error.setErrorMessage("Persistence error: " + t.getMessage());
        fileValidator.saveValidationErrors(List.of(error));
    }

    @Override
    public void onSkipInProcess(TransactionBatchProcessDto item, Throwable t) {
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


