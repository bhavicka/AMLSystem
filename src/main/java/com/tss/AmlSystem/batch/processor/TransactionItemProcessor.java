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
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.SmartValidator;

import java.util.List;

@Component
@RequiredArgsConstructor
@StepScope
public class TransactionItemProcessor implements ItemProcessor<TransactionBatchProcessDto, Transaction>, StepExecutionListener {

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
        int rowNumber = currentRowNumber();

        DataBinder binder = new DataBinder(dto);
        binder.setValidator(smartValidator);
        binder.validate();
        BindingResult results = binder.getBindingResult();

        if (results.hasErrors()) {
            handleValidationErrors(results, fileEntity, rowNumber);
            return null;
        }
        if (!accountRepository.existsByAccountNumber(dto.accountNumber().trim())) {
            // Save a custom error to your table
//            validationService.saveManualError(
//                    fileEntity,
//                    rowNumber,
//                    "accountNumber",
//                    "Account number " + dto.accountNumber() + " already exists in the system."
//            );
            return null; // Skip this row
        }

        Transaction transaction = transactionMapper.toTransaction(dto);
        transaction.setFile(fileEntity);
        return transaction;
    }

    private int currentRowNumber() {
        StepExecution stepExecution = StepSynchronizationManager.getContext().getStepExecution();
        return (int)(stepExecution.getReadCount() + 1);
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

