package com.tss.AmlSystem.batch.processor;

import com.tss.AmlSystem.dto.request.AccountBatchProcessDto;
import com.tss.AmlSystem.dto.request.CustomerBatchProcessDto;
import com.tss.AmlSystem.entity.enums.Severity;
import com.tss.AmlSystem.entity.enums.tenant.FileType;
import com.tss.AmlSystem.entity.enums.tenant.OccupationType;
import com.tss.AmlSystem.entity.tenant.Account;
import com.tss.AmlSystem.entity.tenant.Customer;
import com.tss.AmlSystem.entity.tenant.File;
import com.tss.AmlSystem.entity.tenant.FileValidationErrors;
import com.tss.AmlSystem.factory.FileValidatorFactory;
import com.tss.AmlSystem.mapper.AccountMapper;
import com.tss.AmlSystem.strategy.batch.file.FileValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.SmartValidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
@StepScope
public class AccountItemProcessor implements ItemProcessor<AccountBatchProcessDto, Account>, StepExecutionListener {

    private final AccountMapper accountMapper;

    private FileValidator fileValidator;
    private final SmartValidator smartValidator;
    private final FileValidatorFactory factory;

    private File fileEntity;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        fileValidator = factory.getValidator(FileType.ACCOUNTS);
        Long fileId = stepExecution.getJobParameters().getLong("fileId");
        this.fileEntity = fileValidator.getFile(fileId);
    }

    @Override
    public Account process(AccountBatchProcessDto dto) {
        int rowNumber = currentRowNumber();

        DataBinder binder = new DataBinder(dto);
        binder.setValidator(smartValidator);
        binder.validate();
        BindingResult results = binder.getBindingResult();

        if (results.hasErrors()) {
            handleValidationErrors(results, fileEntity, rowNumber);
            return null;
        }
        Account account = accountMapper.toAccount(dto);
        account.setFile(fileEntity);
        return account;
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
