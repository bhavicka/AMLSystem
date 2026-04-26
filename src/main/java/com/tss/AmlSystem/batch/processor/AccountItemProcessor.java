package com.tss.AmlSystem.batch.processor;

import com.tss.AmlSystem.dto.request.AccountBatchProcessDto;
import com.tss.AmlSystem.entity.tenant.Account;
import com.tss.AmlSystem.entity.tenant.File;
import com.tss.AmlSystem.entity.tenant.FileValidationErrors;
import com.tss.AmlSystem.mapper.AccountMapper;
import com.tss.AmlSystem.service.FileValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.util.List;

@Component
@RequiredArgsConstructor
@StepScope
public class AccountItemProcessor implements ItemProcessor<AccountBatchProcessDto, Account>, StepExecutionListener {

    private final FileValidationService fileValidationService;
    private final AccountMapper accountMapper;
    private File fileEntity;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        Long fileId = stepExecution.getJobParameters().getLong("fileId");
        this.fileEntity = fileValidationService.getFile(fileId);
    }

    @Override
    public Account process(AccountBatchProcessDto dto) {
        int rowNumber = currentRowNumber();
        List<FileValidationErrors> errors = fileValidationService.validateAccount(dto, fileEntity, rowNumber);

        if (!errors.isEmpty()) {
            fileValidationService.saveValidationErrors(errors);
            return null;
        }

        return accountMapper.toAccount(dto);
    }

    private int currentRowNumber() {
        StepExecution stepExecution = StepSynchronizationManager.getContext().getStepExecution();
        return (int)(stepExecution.getReadCount() + 1);
    }

    private String normalizeOptional(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
